package app.slyworks.auth_lib

import android.app.Activity
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.models_commons_lib.models.AccountType
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.models_commons_lib.models.TempUserDetails
import app.slyworks.utils_lib.CompressImageCallable
import app.slyworks.utils_lib.IDHelper
import app.slyworks.utils_lib.TaskManager
import app.slyworks.utils_lib.utils.onNextAndComplete
import app.slyworks.utils_lib.utils.plusAssign
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class RegistrationManager(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseMessaging: FirebaseMessaging,
    private val firebaseUtils: FirebaseUtils,
    private val taskManager: TaskManager,
    private val cryptoHelper:CryptoHelper,
    private val authStateListener: MAuthStateListener) {

    //region Vars
    private lateinit var user: TempUserDetails
    private var currentFirebaseUserResult: AuthResult? = null

    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    fun unbind():Unit = disposables.clear()

    fun register(userDetails: TempUserDetails): Single<Outcome> {
        user = userDetails
        return cryptoHelper.initialize()
            .concatMap {
                when{
                    it.first -> createFirebaseUser()
                    else -> Single.just(Outcome.FAILURE<String>(value = "retrieving config details failed", reason = it.second as String))
                }
            }
            .concatMap {
                when {
                    it.isSuccess -> uploadUserProfileImage()
                    else -> deleteUser().concatMap { _ -> Single.just(it) }
                }
            }.concatMap {
                when {
                    it.isSuccess -> downloadImageUrl()
                    else -> deleteUser().concatMap { _ -> Single.just(it) }
                }
            }.concatMap { it ->
                when {
                    it.isSuccess -> createAgoraUser()
                    else -> deleteUserProfileImage().concatMap { _ -> Single.just(it) }
                }
            }.concatMap {
                when {
                    it.isSuccess -> getFCMRegistrationToken()
                    else -> Single.just(it)
                }
            }.concatMap {
                when {
                    it.isSuccess -> uploadUserDetailsToFirebaseDB()
                    else -> deleteUserDetailsFromDB().concatMap { _ -> Single.just(it) }
                }
            }.concatMap {
                when{
                    it.isSuccess -> signOutCreatedUser()
                    else -> Single.just(it)
                }
            }

    }

    private fun createFirebaseUser(): Single<Outcome> =
        cryptoHelper.hashAsync(user.password!!)
            .concatMap { signupOnFB(user.email!!, it) }

    private fun signupOnFB(email:String, hashedPassword:String):Single<Outcome> =
        Single.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email, hashedPassword)
                .addOnCompleteListener {
                    val r:Outcome

                    if (it.isSuccessful) {
                        currentFirebaseUserResult = it.result!!
                        user.firebaseUID = it.result!!.user!!.uid
                        Timber.e("_createFirebaseUser: createUserWithEmailAndPassword() successful")
                        r = Outcome.SUCCESS(value = "firebase user created successfully")
                    } else {
                        Timber.e("_createFirebaseUser: createUserWithEmailAndPassword() completed but was not successful")

                        r = Outcome.FAILURE(
                            value = "firebase user was not created",
                            reason = it.exception?.message ?: "user was not created")
                    }

                    emitter.onSuccess(r)
                }
        }

    private fun signOutCreatedUser():Single<Outcome> =
        Single.fromCallable{
            firebaseAuth.signOut()
            Outcome.SUCCESS(value = "created user logged out successfully")
        }

    private fun uploadUserProfileImage(): Single<Outcome> {
        return Single.create { emitter ->
            try {
                val imageByteArray: ByteArray = taskManager.runOnThreadPool(CompressImageCallable(user.image_uri_init!!))

                val storageReference = firebaseUtils.getUserProfileImageStorageRef(currentFirebaseUserResult!!.user!!.uid)

                storageReference
                    .putBytes(imageByteArray)
                    .addOnCompleteListener {
                        val r:Outcome

                        if (it.isSuccessful) {
                            r = Outcome.SUCCESS(value = "user profile image uploaded successfully")
                        } else {
                            Timber.e("_uploadUserProfileImage: query uploading user image completed but wasn't successful")
                            r = Outcome.FAILURE(
                                value = "user profile image was not uploaded",
                                reason = it.exception?.message ?: "profile image was not uploaded")
                        }

                        emitter.onSuccess(r)
                    }
            } catch (e: Exception) {
                Timber.e("_uploadUserProfileImage: _uploadUserProfileImage().catch(): error occurred", e)

                val r = Outcome.FAILURE(
                    value = "an error occurred uploading user profile image",
                    reason = e.message)
                emitter.onSuccess(r)
            }
        }
    }

    private fun downloadImageUrl():Single<Outcome> =
        Single.create { emitter ->
            val storageReference = firebaseUtils.getUserProfileImageStorageRef(currentFirebaseUserResult!!.user!!.uid)

            storageReference.downloadUrl
                .addOnCompleteListener { it ->
                    val r:Outcome

                    if (it.isSuccessful) {
                        user.imageUri = it.result.toString()
                        r = Outcome.SUCCESS(value = "user profile image url downloaded successfully")
                    } else {
                        Timber.e("_uploadUserProfileImage: query retrieving downloadUrl for image completed but wasn't successful")
                        r = Outcome.FAILURE(
                            value = "user profile image url was not downloaded",
                            reason = it.exception?.message ?: "profile image url was not downloaded")
                    }

                    emitter.onSuccess(r)
                }
        }

    private fun createAgoraUser(): Single<Outcome> =
        with(IDHelper.generateNewUserID()) {
            user.agoraUID = this
            Single.just(Outcome.SUCCESS(value = "user agoraID generated successfully", additionalInfo = this))
        }

    private fun getFCMRegistrationToken(): Single<Outcome> {
        return Single.create { emitter ->
            firebaseMessaging
                .getToken()
                .addOnCompleteListener {
                    val r:Outcome

                    if (it.isSuccessful) {
                        user.FBRegistrationToken = it.result

                        r = Outcome.SUCCESS(value = "getting user FCM registration token", additionalInfo = it.result)
                    } else {
                        Timber.e("getFCMRegistrationToken: getting user FCM Registration token completed but failed")
                        //AppController.notifyObservers(EVENT_USER_REGISTRATION, false)

                        r = Outcome.FAILURE(
                            value = "getting user FCM registration token failed",
                            reason = it.exception?.message ?: "getting FCM token failed")
                    }

                    emitter.onSuccess(r)
                }
        }
    }

    private fun uploadUserDetailsToFirebaseDB(): Single<Outcome> {
        return Single.create { emitter ->
            val user: FBUserDetailsVModel = parseUserDetails()
            val databaseAddress: String = currentFirebaseUserResult!!.user!!.uid

            firebaseUtils.getUserDataForUIDRef(databaseAddress)
                .setValue(user)
                .addOnCompleteListener {
                    val r:Outcome

                    if (it.isSuccessful) {
                        r = Outcome.SUCCESS(value = "user details successfully uploaded")
                    } else {
                        Timber.e("uploadUserDetailsToFirebaseDB2: uploading user details to multiple locations in the DB, completed but failed")
                        r = Outcome.FAILURE(
                            value = "uploading user details to Firebase failed",
                            reason = it.exception?.message ?: "uploading user details failed")
                    }

                    emitter.onSuccess(r)
                }
        }
    }

    private fun parseUserDetails(): FBUserDetailsVModel {
        val user = FBUserDetailsVModel(
            user.accountType.toString(),
            user.firstName!!,
            user.lastName!!,
            "${user.firstName} ${user.lastName}",
            user.email!!,
            user.sex.toString(),
            user.age!!,
            user.firebaseUID!!,
            user.agoraUID!!,
            user.FBRegistrationToken!!,
            user.imageUri!!,
            null,
            null)

        if (this.user.accountType == AccountType.PATIENT)
            user.history = this.user.history
        else
            user.specialization = this.user.specialization

        return user
    }

    private fun deleteUser(user:TempUserDetails = this.user):Single<Outcome>{
        if(firebaseAuth.currentUser == null)
            return Single.just(Outcome.FAILURE(value = false, reason = "no user currently signed in"))

        return Single.create{ emitter ->
            firebaseAuth.currentUser!!
                .delete()
                .addOnCompleteListener {
                    val r:Outcome

                    if(it.isSuccessful){
                        Timber.e("deleteUser: delete successful")

                        r = Outcome.SUCCESS(value = true)
                    }else{
                        Timber.e("deleteUser: delete unsuccessful", it.exception)

                        r = Outcome.FAILURE(
                            value = false,
                            reason = it.exception?.message ?: "deleting user failed")
                    }

                    emitter.onSuccess(r)
                }
        }

    }

    private fun deleteUserProfileImage():Single<Outcome>{
        if(currentFirebaseUserResult == null || currentFirebaseUserResult!!.user?.uid == null)
            return Single.just(Outcome.FAILURE(value = false, reason = "no user currently signed in"))

        return Single.create { emitter ->
            val storageReference = firebaseUtils.getUserProfileImageStorageRef(currentFirebaseUserResult!!.user!!.uid)
            storageReference
                .delete()
                .addOnCompleteListener {
                    val r:Outcome

                    if(it.isSuccessful){
                        Timber.e("deleteUserProfileImage: delete successful")
                        r = Outcome.SUCCESS(value = true)

                    }else{
                        Timber.e("deleteUserProfileImage: delete unsuccessful", it.exception )
                        r = Outcome.FAILURE(
                            value = false,
                            reason = it.exception?.message ?: "deleting profile image failed")
                    }

                    emitter.onSuccess(r)
                }
        }

    }

    private fun deleteUserDetailsFromDB():Single<Outcome>{
        if(currentFirebaseUserResult?.user?.uid == null)
            return Single.just(Outcome.FAILURE(value = false, reason = "no user currently signed in"))

        return Single.create { emitter ->
            val databaseAddress: String = currentFirebaseUserResult!!.user!!.uid
            firebaseUtils.getUserDataForUIDRef(databaseAddress)
                .setValue(null)
                .addOnCompleteListener {
                    val r:Outcome

                    if(it.isSuccessful){
                        Timber.e("deleteUserDetailsFromDB: delete successful" )

                        r = Outcome.SUCCESS(value = true)
                    }else{
                        Timber.e("deleteUserDetailsFromDB: delete unsuccessful")

                        r = Outcome.FAILURE(
                            value = false,
                            reason = it.exception?.message ?: "deleting user details unsuccessful")
                    }

                    emitter.onSuccess(r)
                }
        }

    }

}

