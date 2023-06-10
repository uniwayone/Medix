package app.slyworks.auth_lib

import app.slyworks.constants_lib.EVENT_GET_DOCTOR_USERS
import app.slyworks.constants_lib.EVENT_UPDATE_FCM_REGISTRATION_TOKEN
import app.slyworks.constants_lib.OUTGOING_MESSAGE
import app.slyworks.controller_lib.AppController
import app.slyworks.data_lib.*
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.FBUserDetailsWrapper
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.data_lib.models.PersonVModel
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.firebase_commons_lib.MChildEventListener
import app.slyworks.firebase_commons_lib.MValueEventListener
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.utils.onNextAndComplete
import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 *Created by Joshua Sylvanus, 3:18 AM, 1/8/2022.
 */
class UsersManager(
    private val firebaseUtils: FirebaseUtils,
    private val dataManager: DataManager) {

    //region Vars
    private var doctorChildEventListener:ChildEventListener? = null
    private var userDataValueEventListener:ValueEventListener? = null
    //endregion

    private fun userDataValueEventListener(o:PublishSubject<FBUserDetailsVModel>):ValueEventListener{
        userDataValueEventListener = MValueEventListener(
            onDataChangeFunc = {
                val userDetails: FBUserDetailsVModel = it.getValue(FBUserDetailsVModel::class.java)!!
                o.onNext(userDetails)
            })

        return userDataValueEventListener!!
    }

    fun observeUserDataForUID(firebaseUID:String): Observable<FBUserDetailsVModel> {
        val o:PublishSubject<FBUserDetailsVModel> = PublishSubject.create()

        firebaseUtils.getUserDataForUIDRef(firebaseUID)
            .addValueEventListener(userDataValueEventListener(o))

        return o.hide()
    }

    fun detachUserDataListener(firebaseUID: String){
       firebaseUtils.getUserDataForUIDRef(firebaseUID)
           .removeEventListener(userDataValueEventListener!!)

        userDataValueEventListener = null
    }

    fun getUserDataForUID(firebaseUID:String):Observable<Outcome> =
        Observable.create { emitter ->
           firebaseUtils.getUserDataForUIDRef(firebaseUID)
               .get()
               .addOnCompleteListener {
                   if (it.isSuccessful){
                       val userDetails: FBUserDetailsVModel = it.result!!.getValue(FBUserDetailsVModel::class.java)!!
                       emitter.onNextAndComplete(Outcome.SUCCESS(value = userDetails))
                   } else{
                       emitter.onNextAndComplete(Outcome.FAILURE(value = it.exception?.message))
                   }
               }
        }

    fun getAllDoctors(){
           firebaseUtils.getAllDoctorsRef()
           .get()
           .addOnCompleteListener {
               if(it.isSuccessful){
                   val list:MutableList<FBUserDetailsVModel> = mutableListOf()
                   it.result!!.children.forEach { child ->
                       Timber.e("getAllDoctors: $child" )
                       val doctor: FBUserDetailsWrapper = child.getValue(FBUserDetailsWrapper::class.java)!!
                       list.add(doctor.details)

                       AppController.notifyObservers(
                           EVENT_GET_DOCTOR_USERS,
                           Outcome.SUCCESS(list))
                   }
               }else{
                   Timber.e("error occurred getting doctor users from DB", it.exception)

                   AppController.notifyObservers(
                       EVENT_GET_DOCTOR_USERS,
                       Outcome.FAILURE(Unit, it.exception?.message))
               }
           }
    }

    private fun doctorChildEventListener(o:PublishSubject<Outcome>):ChildEventListener{
           doctorChildEventListener = MChildEventListener(
               onChildAddedFunc = { snapshot ->
                   val list: MutableList<FBUserDetailsVModel> = mutableListOf()
                   snapshot.children.forEach {
                       val user = snapshot.getValue(FBUserDetailsVModel::class.java)
                       list.add(user!!)
                   }

                   o.onNext(Outcome.SUCCESS(list))
               })

           return doctorChildEventListener!!
    }

    fun observeDoctors():Observable<Outcome>{
        val o = PublishSubject.create<Outcome>()
        firebaseUtils.getAllDoctorsRef()
            .addChildEventListener(doctorChildEventListener(o))

        return o.hide()
    }

    fun detachGetAllDoctorsListener() {
         firebaseUtils.getAllDoctorsRef()
             .removeEventListener(doctorChildEventListener!!)

        doctorChildEventListener = null
    }

    fun clearUserDetails():Unit{
        dataManager.clearUserDetailsFromDataStore()
            .subscribe()
    }


    fun sendFCMTokenToServer(token:String){
        val address:String =
        if(!dataManager.getUserDetailsParam<String>("firebaseUID").isNullOrEmpty())
            dataManager.getUserDetailsParam("firebaseUID")!!
        else return

       firebaseUtils.getFCMRegistrationTokenRef(address)
            .setValue(token)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    AppController.notifyObservers(EVENT_UPDATE_FCM_REGISTRATION_TOKEN, true)
                }else{
                    Timber.e("sendFCMTokenToServer: uploading FCMToken to DB failed", it.exception)
                    AppController.notifyObservers(EVENT_UPDATE_FCM_REGISTRATION_TOKEN, true)
                }
            }
    }

    fun updateUserInfo(details: FBUserDetailsVModel):Observable<Boolean> =
        getMessagesForUIDObservable(details)
            .flatMap {
                if(it)
                    getUpdatePersonObservable(details)
                else
                    Observable.just(false)
            }

    private fun getMessagesForUIDObservable(details: FBUserDetailsVModel):Observable<Boolean> =
        Observable.create { emitter ->
                val l:List<MessageVModel> =
                    dataManager.getMessagesForUID(details.firebaseUID)

                if(l.isNullOrEmpty()) {
                    emitter.onNextAndComplete(false)
                    return@create
                }

                l.map {
                    it.accountType = details.accountType
                    it.senderImageUri = details.imageUri
                    if(it.type == OUTGOING_MESSAGE)
                        it.senderFullName = details.fullName
                    else
                        it.receiverFullName = details.fullName

                    it
                }

               dataManager.updateMessages(*l.toTypedArray())

              emitter.onNextAndComplete(true)
        }

    private fun getUpdatePersonObservable(details: FBUserDetailsVModel):Observable<Boolean> =
        Observable.create { emitter ->
            CoroutineScope(Dispatchers.IO).launch {
                val p: PersonVModel? =
                    dataManager.getPersonByID(details.firebaseUID)

                if(p == null){
                    emitter.onNext(false)
                    emitter.onComplete()
                    this.cancel()
                }

                p!!
                p.fullName = details.fullName
                p.imageUri = details.imageUri

               dataManager.updatePersons(p)
            }
        }

}