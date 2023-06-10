package app.slyworks.communication_lib

import app.slyworks.constants_lib.*
import app.slyworks.controller_lib.AppController
import app.slyworks.data_lib.models.ConsultationRequestVModel
import app.slyworks.data_lib.DataManager
import com.google.firebase.database.*
import app.slyworks.fcm_api_lib.FCMClientApi
import app.slyworks.fcm_api_lib.FirebaseCloudMessage
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.firebase_commons_lib.MChildEventListener
import app.slyworks.firebase_commons_lib.MValueEventListener
import app.slyworks.models_commons_lib.models.*
import app.slyworks.utils_lib.utils.onNextAndComplete
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


/**
 * Created by Joshua Sylvanus, 10:16 PM, 1/19/2022.
 */
class ConsultationRequestsManager(
    //region Vars
    private val firebaseDatabase: FirebaseDatabase,
    private val fcmClientApi: FCMClientApi,
    private val firebaseUtils: FirebaseUtils,
    private val dataManager: DataManager) {

    private lateinit var consultationRequestsValueEventListener:ValueEventListener
    private lateinit var consultationRequestChildEventListener:ChildEventListener
    //endregion

    fun getAllConsultationRequests():Observable<Outcome>
    = Observable.just(dataManager.getConsultationRequests())
                .concatMap {
                    if(it.isNotEmpty())
                        return@concatMap dataManager.observeConsultationRequests()
                                            .toObservable()
                                            .concatMap { it2:List<ConsultationRequestVModel> -> Observable.just(Outcome.SUCCESS(value = it2)) }

                    else
                        return@concatMap  dataManager.observeConsultationRequests()
                                            .toObservable()
                                            .concatMap { it3:List<ConsultationRequestVModel> -> Observable.just(Outcome.SUCCESS(value = it3)) }
                                            .startWithItem(Outcome.FAILURE(value = Unit, reason = "you currently have no consultation requests"))
                }


    /*TODO:there is a function that observes the consultation request for a SPECIFIC user,
    *   create one that listens for ALL consultation request status,
    *  the refs have been created in FirebaseUtils*/
    fun listenForConsultationRequests():Observable<ConsultationRequestVModel> =
        Observable.create { emitter:ObservableEmitter<ConsultationRequestVModel> ->
            consultationRequestChildEventListener =
                MChildEventListener(
                    onChildAddedFunc = { snapshot ->
                        val request: ConsultationRequestVModel = snapshot.getValue(ConsultationRequestVModel::class.java)!!
                        emitter.onNext(request)

                        /* save to DB */
                        dataManager.addConsultationRequests(request)
                    })

          firebaseUtils.getUserReceivedConsultationRequestsRef(dataManager.getUserDetailsParam<String>("firebaseUID")!!)
                .addChildEventListener(consultationRequestChildEventListener)
        }

    fun detachConsultationRequestListener(){
         firebaseUtils.getUserReceivedConsultationRequestsRef(dataManager.getUserDetailsParam<String>("firebaseUID")!!)
            .removeEventListener(consultationRequestChildEventListener)
    }

    fun observeConsultationRequestStatus(UID:String):Observable<String>
    = Observable.create<String>{ emitter ->
            consultationRequestsValueEventListener =
                MValueEventListener(
                    onDataChangeFunc = {
                        //"REQUEST_PENDING,REQUEST_ACCEPTED,REQUEST_DECLINED,("NOT_SENT")
                        val result: ConsultationRequestVModel? = it.getValue<ConsultationRequestVModel>(
                            ConsultationRequestVModel::class.java
                        )

                        val status: String =
                            if (result == null)
                                REQUEST_NOT_SENT
                            else if (result.status == null)
                                REQUEST_NOT_SENT
                            else result.status

                        emitter.onNext(status)
                    })

          firebaseUtils.getUserSentConsultationRequestsRef(
                params = dataManager.getUserDetailsParam<String>("firebaseUID")!!,
                params2 = UID)
            .addValueEventListener(consultationRequestsValueEventListener)
        }

    fun detachCheckRequestStatusListener(UID:String){
      firebaseUtils.getUserSentConsultationRequestsRef(
            params = dataManager.getUserDetailsParam<String>("firebaseUID")!!,
            params2 = UID)
            .removeEventListener(consultationRequestsValueEventListener)
    }

    /* to do make this an atomic process */
    fun sendConsultationRequestResponse(response: ConsultationResponse):Observable<Outcome>
    = Observable.zip(
            sendConsultationRequestResponseViaFCM(response),
            sendConsultationRequestResponseToDB(response))
        { o1: Outcome, o2: Outcome ->
            if (o1.isSuccess && o2.isSuccess)
                Outcome.SUCCESS<Nothing>(additionalInfo = "response delivered")
            else
                Outcome.FAILURE<Nothing>(reason = "response was not successfully delivered")
        }

    private fun sendConsultationRequestResponseViaFCM(response: ConsultationResponse):Observable<Outcome>
       =  Observable.create<Outcome> { emitter ->
           val fcMessage: app.slyworks.fcm_api_lib.FirebaseCloudMessage = mapConsultationResponseToFCMessage(response)

            fcmClientApi
                .sendCloudMessage(fcMessage)
                .enqueue(object : Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Timber.e( "onResponse: cloud message consultation request sent successfully")
                            emitter.onNextAndComplete(Outcome.SUCCESS(null))
                        } else {
                            Timber.e( "onResponse: cloud message consultation request did not send successfully")
                            emitter.onNextAndComplete(Outcome.FAILURE(null))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Timber.e("onFailure: sending cloud message response failed", t)
                        emitter.onNextAndComplete(Outcome.ERROR(null, additionalInfo = t.message))
                    }
                })
        }
        .onErrorReturn{ it:Throwable -> Outcome.FAILURE<Unit>(reason = it.message) }

    private fun sendConsultationRequestResponseToDB(response: ConsultationResponse):Observable<Outcome>
    = Observable.create<Outcome> { emitter ->
            val childNodeUpdate:HashMap<String, Any> = hashMapOf(
                "/requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/from/${response.toUID}/status" to response.status,
                "/requests/${response.toUID}/to/${dataManager.getUserDetailsParam<String>("firebaseUID")}/status" to response.status)

            val childNodeUpdateNull:HashMap<String, Any?> = hashMapOf(
                "/requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/from/${response.toUID}/status" to null,
                "/requests/${response.toUID}/to/${dataManager.getUserDetailsParam<String>("firebaseUID")}/status" to null)

        with(firebaseDatabase.reference){
            updateChildren(childNodeUpdate)
                .addOnSuccessListener {
                    Timber.e("sendConsultationRequestResponse: success")
                    emitter.onNext(Outcome.SUCCESS(null))
                    emitter.onComplete()
                }
                .addOnFailureListener { _ ->
                     updateChildren(childNodeUpdateNull)
                         .addOnCompleteListener {
                             emitter.onNext(Outcome.FAILURE(null))
                             emitter.onComplete()
                         }
                }
          }
      }
      .onErrorReturn { it:Throwable -> Outcome.FAILURE<Unit>(reason = it.message)  }


    fun sendConsultationRequest(request: ConsultationRequestVModel, mode: MessageMode = MessageMode.DB_MESSAGE){
        when(mode){
            MessageMode.DB_MESSAGE -> sendConsultationRequestViaFB(request)
            MessageMode.CLOUD_MESSAGE -> sendConsultationRequestViaFCM(request)
        }
    }

    private fun sendConsultationRequestViaFB(request: ConsultationRequestVModel) {
        val childNodeUpdate:HashMap<String, Any> = hashMapOf(
            "/requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/to/${request.toUID}" to request,
            "/requests/${request.toUID}/from/${dataManager.getUserDetailsParam<String>("firebaseUID")}" to request)

        firebaseDatabase
            .reference
            .updateChildren(childNodeUpdate)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    dataManager.addConsultationRequests(request)

                    AppController.notifyObservers(EVENT_SEND_REQUEST, true)
                }else{
                    Timber.e("sendRequest: sending request failed", it.exception)

                    dataManager.addConsultationRequests(request.apply { this.status = REQUEST_NOT_SENT })

                    AppController.notifyObservers(EVENT_SEND_REQUEST, false)
                }
            }
    }

    private fun sendConsultationRequestViaFCM(request: ConsultationRequestVModel){
        CoroutineScope(Dispatchers.IO).launch {
            val fcMessage: FirebaseCloudMessage = mapConsultationRequestToFCMessage(request)

            fcmClientApi
                .sendCloudMessage(fcMessage)
                .enqueue(object: Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if(response.isSuccessful) {
                            sendConsultationRequestResponseToDB(
                                ConsultationResponse(toUID = fcMessage.to,
                                                     fromUID = dataManager.getUserDetailsParam<String>("firebaseUID")!!,
                                                     fullName = request.details.fullName))

                            dataManager.addConsultationRequests(request)

                            AppController.notifyObservers(EVENT_SEND_REQUEST, true)
                        }
                        else {
                            dataManager.addConsultationRequests(request.apply { this.status = REQUEST_NOT_SENT })

                            AppController.notifyObservers(EVENT_SEND_REQUEST, false)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Timber.e("onFailure: sending Cloud message request failed",t)

                        dataManager.addConsultationRequests(request.apply { this.status = REQUEST_NOT_SENT })

                        AppController.notifyObservers(EVENT_SEND_REQUEST, false)
                    }
                })
        }
    }

    private fun mapConsultationResponseToFCMessage(response: ConsultationResponse): FirebaseCloudMessage {
        val _type:String = if(response.status == REQUEST_ACCEPTED) FCM_RESPONSE_ACCEPTED else FCM_RESPONSE_DECLINED
        val action:String = if(response.status == REQUEST_ACCEPTED) "accepted" else "declined"
        val message:String = "${response.fullName} $action your request for consultation"
        val data: Data =
            ConsultationRequestData(message = message,
                                    fromUID =  response.fromUID,
                                     fullName = response.fullName,
                                     toFCMRegistrationToken = response.toFCMRegistrationToken,
                                     type = _type)
        return FirebaseCloudMessage(response.toFCMRegistrationToken, data)
    }

    private fun mapConsultationRequestToFCMessage(request: ConsultationRequestVModel): app.slyworks.fcm_api_lib.FirebaseCloudMessage {
        val message: String = "Hi i'm ${dataManager.getUserDetailsParam<String>("fullName")}. Please i would like a consultation with you"
        val data: Data =
            ConsultationRequestData(message = message,
                                    fromUID = request.details.firebaseUID,
                                    fullName = request.details.fullName,
                                     toFCMRegistrationToken = request.details.FCMRegistrationToken,
                                    type = FCM_REQUEST)
        return FirebaseCloudMessage(request.details.FCMRegistrationToken, data)
    }

    fun updateRequestSender(toUID:String, status:String){
        val childNodes:HashMap<String, Any> = hashMapOf(
            "/requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/to/$toUID/status" to status,
            "/requests/$toUID/from/${dataManager.getUserDetailsParam<String>("firebaseUID")}/status" to status )

        firebaseDatabase
            .reference
            .updateChildren(childNodes)
            .addOnCompleteListener {
                if(it.isSuccessful)
                    Timber.e("updateRequest_sender: updates REQUEST status in DB successful" )
                else
                    Timber.e("updateRequest_sender: update REQUEST status in DB failed", it.exception )
            }

    }


}