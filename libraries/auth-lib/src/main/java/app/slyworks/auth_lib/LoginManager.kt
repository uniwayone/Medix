package app.slyworks.auth_lib

import app.slyworks.constants_lib.KEY_FCM_REGISTRATION
import app.slyworks.constants_lib.KEY_IS_THERE_NEW_FCM_REG_TOKEN
import app.slyworks.constants_lib.KEY_LAST_SIGN_IN_TIME
import app.slyworks.constants_lib.KEY_LOGGED_IN_STATUS
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.PreferenceManager
import app.slyworks.utils_lib.TimeHelper
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import kotlin.jvm.Throws


class LoginManager(
    private val preferenceManager: PreferenceManager,
    private val firebaseAuth:FirebaseAuth,
    private val usersManager: UsersManager,
    private val firebaseUtils: FirebaseUtils,
    private val timeHelper: TimeHelper,
    private val cryptoHelper: CryptoHelper,
    private val dataManager: DataManager,
    private val authStateListener: MAuthStateListener) {

    //region Vars
    private var loggedInStatus:Boolean = false
    //endregion

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun handleForgotPassword(email: String): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    val r:Boolean

                    if (it.isSuccessful) {
                        Timber.e("handleForgotPassword: password reset email successfully sent")
                        r = true
                    } else {
                        Timber.e("handleForgotPassword: password reset email was not successfully sent", it.exception)
                        r = false
                    }

                    emitter.onSuccess(r)
                }
        }
    }

    fun getLoginStatus():Boolean = loggedInStatus

    fun getLoginStatus2():Boolean {
        return preferenceManager.get(KEY_LOGGED_IN_STATUS, false)!!  &&
                with(preferenceManager.get(KEY_LAST_SIGN_IN_TIME, System.currentTimeMillis())){
                    timeHelper.isWithin3DayPeriod(this!!)
                }
    }

    fun loginUser(email:String, password:String): Single<Outcome> {
        firebaseAuth.signOut()

       return cryptoHelper.initialize()
            .concatMap {
                if (it.first)
                    cryptoHelper.hashAsync(password)
                else
                    Single.just(Outcome.FAILURE("retrieving config details failed", it.second as String))
            }
            .concatMap {
                if (it is String)
                    signInUser(email, it)
                else
                    Single.just(it as Outcome)
            }
            .concatMap {
                if (it.isSuccess)
                    checkVerificationStatus()
                else
                    Single.just(it)
            }
            .concatMap {
                return@concatMap when{
                    it.isSuccess -> uploadFCMRegistrationToken()
                    it.isFailure -> Single.just(it)
                    it.isError ->
                        uploadFCMRegistrationToken()
                            .concatMap { it2:Outcome ->
                                /* kind of including the second error(if there is) and the first */
                               Single.just(Outcome.ERROR(it2.getAdditionalInfo(), it.getAdditionalInfo()))
                            }
                    else -> throw IllegalArgumentException("don't know the Outcome type")
                }
            }
            .concatMap {
                return@concatMap when{
                    it.isSuccess -> retrieveUserDetails()
                    it.isFailure -> Single.just(it)
                    it.isError ->
                        uploadFCMRegistrationToken()
                            .concatMap { it2:Outcome ->
                                /* kind of including the second error and the first */
                                Single.just(Outcome.ERROR(it2.getAdditionalInfo(), it.getAdditionalInfo()))
                            }
                    else -> throw IllegalArgumentException("don't know the Outcome type")
                }
            }
    }

    private fun checkVerificationStatus():Single<Outcome> =
       Single.create { emitter ->
           firebaseUtils.getUserVerificationStatusRef(firebaseAuth.currentUser!!.uid)
               .get()
               .addOnCompleteListener {
                   if(!it.isSuccessful){
                       emitter.onSuccess(Outcome.FAILURE("sign in failed", it.exception?.message))
                   }else{
                       var isVerified: Boolean = false
                       if (it.result?.exists() == true)
                           isVerified = it.result!!.getValue(Boolean::class.java)!!

                       if (isVerified || firebaseAuth.currentUser!!.isEmailVerified) {
                           preferenceManager.set(KEY_LAST_SIGN_IN_TIME, System.currentTimeMillis())
                           emitter.onSuccess(Outcome.SUCCESS("user is verified"))
                       }else{
                           preferenceManager.set(KEY_LAST_SIGN_IN_TIME, System.currentTimeMillis())
                           /* using Outcome.ERROR as special case */
                           emitter.onSuccess(Outcome.ERROR("please verify your account before you can login", "please verify your account before you can login"))
                       }
                   }
               }
       }

    private fun signInUser(email:String, password: String):Single<Outcome> =
        Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        emitter.onSuccess(Outcome.SUCCESS(value = "login attempt successful"))
                    else
                        emitter.onSuccess(Outcome.FAILURE(value = "login attempt was not successful, please try again", it.exception?.message))
                }
        }

    private fun uploadFCMRegistrationToken():Single<Outcome> =
        /* upload the FCMRegistration token specific to this phone, since
        * its not based on FirebaseUID, but on device */
        Single.create { emitter ->
           val isThereNewToken = preferenceManager.get(KEY_IS_THERE_NEW_FCM_REG_TOKEN, false)!!
           if(!isThereNewToken)
               emitter.onSuccess(Outcome.SUCCESS(value = "no token to upload"))

           val fcmToken:String? = preferenceManager.get(KEY_FCM_REGISTRATION)!!

            firebaseUtils.getUserDataRef(firebaseAuth.currentUser!!.uid)
                .setValue(fcmToken)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        preferenceManager.set(KEY_IS_THERE_NEW_FCM_REG_TOKEN, false)
                        emitter.onSuccess(Outcome.SUCCESS(value = "token uploaded successfully"))
                    }else
                        emitter.onSuccess(Outcome.FAILURE(value = "token was not successfully uploaded", reason = it.exception?.message))
                }
        }

    private fun retrieveUserDetails():Single<Outcome> =
        Single.create { emitter ->
           firebaseUtils.getUserDataForUIDRef(firebaseAuth.currentUser!!.uid)
               .get()
               .addOnCompleteListener {
                   if(it.isSuccessful){
                       val user = it.result!!.getValue(FBUserDetailsVModel::class.java)

                       dataManager.saveUserToDataStore(user!!).subscribe()

                       val r:Outcome = Outcome.SUCCESS(value = "login successful")
                       emitter.onSuccess(r)
                   }else{
                       Timber.e("signInUser: user login failed",it.exception )
                       val r:Outcome = Outcome.FAILURE(value = "oops something went wrong on our end, please try again")
                       emitter.onSuccess(r)
                   }
               }
         }


    fun logoutUser(){
            firebaseAuth.signOut()
            usersManager.clearUserDetails()
            dataManager.clearUserDetailsFromDataStore().subscribe()
            preferenceManager.clearPreference(KEY_LAST_SIGN_IN_TIME)
    }

    fun onDestroy(){
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}