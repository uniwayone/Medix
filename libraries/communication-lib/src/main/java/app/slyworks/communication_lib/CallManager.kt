package app.slyworks.communication_lib

import app.slyworks.constants_lib.TYPE_REQUEST
import app.slyworks.constants_lib.TYPE_RESPONSE
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase
import app.slyworks.fcm_api_lib.FCMClientApi
import app.slyworks.fcm_api_lib.FirebaseCloudMessage
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.data_lib.models.VideoCallRequest
import app.slyworks.data_lib.models.VoiceCallData
import app.slyworks.data_lib.models.VoiceCallRequest
import app.slyworks.utils_lib.utils.onNextAndComplete
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class CallManager
constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val fcmClientApi: FCMClientApi,
    private val firebaseUtils: FirebaseUtils,
    private val dataManager: DataManager) {

    //region Vars
    private var videoCallObserver:PublishSubject<FBUserDetailsVModel>? = PublishSubject.create()
    private var voiceCallObserver:PublishSubject<FBUserDetailsVModel>? = PublishSubject.create()

    private var videoCallRequestsChildEventListener:ChildEventListener? = null
    private var voiceCallRequestsChildEventListener:ChildEventListener? = null
    //endregion

    fun listenForVideoCallRequests(): Observable<FBUserDetailsVModel> {
        if(videoCallRequestsChildEventListener == null){
            videoCallRequestsChildEventListener = app.slyworks.firebase_commons_lib.MChildEventListener(
                onChildChangedFunc = { snapshot ->
                    val request: VideoCallRequest = snapshot.getValue(VideoCallRequest::class.java)!!

                    if (videoCallObserver == null)
                        videoCallObserver = PublishSubject.create()

                    videoCallObserver?.onNext(request.details)
                })
        }

        firebaseUtils.getVideoCallRequestsRef(dataManager.getUserDetailsParam("firebaseUID")!!)
            .addChildEventListener(videoCallRequestsChildEventListener!!)

        return videoCallObserver!!
    }

    fun detachVideoCallRequestsListener(){
        firebaseUtils.getVideoCallRequestsRef(dataManager.getUserDetailsParam<String>("firebaseUID")!!)
            .removeEventListener(videoCallRequestsChildEventListener!!)

        videoCallRequestsChildEventListener = null
        videoCallObserver = null
    }

    fun processVideoCall(type:String,
                         firebaseUID: String,
                         status:String? = null,
                         request: VideoCallRequest? = null){
        processVideoCallAsync(type = type,
            firebaseUID = firebaseUID,
            status = status,
            request = request)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { _ -> }
    }


    fun processVideoCallAsync(type:String,
                              firebaseUID: String,
                              status:String? = null,
                              request: VideoCallRequest? = null):Observable<Outcome> =
        Observable.create { emitter ->
            var childNodeUpdate:HashMap<String, Any> = hashMapOf()
            when(type){
                TYPE_REQUEST -> {
                    childNodeUpdate = hashMapOf(
                        "/video_call_requests/$firebaseUID/from/${dataManager.getUserDetailsParam<String>("firebaseUID")}" to request!!,
                        "/video_call_requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/to/$firebaseUID" to request
                    )
                }
                TYPE_RESPONSE ->{
                    childNodeUpdate = hashMapOf(
                        "/video_call_requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/from/$firebaseUID/status" to status!!,
                        "/video_call_requests/$firebaseUID/to/${dataManager.getUserDetailsParam<String>("firebaseUID")}/status" to status)

                }
            }

            firebaseDatabase
                .reference
                .updateChildren(childNodeUpdate)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val r:Outcome = Outcome.SUCCESS<Nothing>()
                        Timber.e( "processVideoCall: video call status updated successfully in DB")
                        emitter.onNextAndComplete(r)
                    } else {
                        val r:Outcome = Outcome.FAILURE<Nothing>()
                        Timber.e( "processVideoCall: video call status updated with issues in DB")
                        emitter.onNextAndComplete(r)
                    }
                }
        }


    fun listenForVoiceCallRequests():Observable<FBUserDetailsVModel>{
        if(voiceCallRequestsChildEventListener == null){
            voiceCallRequestsChildEventListener = app.slyworks.firebase_commons_lib.MChildEventListener(
                onChildChangedFunc = { snapshot ->
                    val request: VoiceCallRequest = snapshot.getValue(VoiceCallRequest::class.java)!!

                    if (voiceCallObserver == null)
                        voiceCallObserver = PublishSubject.create()

                    voiceCallObserver?.onNext(request.details)
                })
        }


        firebaseUtils.getVideoCallRequestsRef(dataManager.getUserDetailsParam<String>("firebaseUID")!!)
            .addChildEventListener(voiceCallRequestsChildEventListener!!)

        return voiceCallObserver!!
    }

    fun detachVoiceCallRequestsListener(){
        firebaseUtils.getVideoCallRequestsRef(dataManager.getUserDetailsParam<String>("firebaseUID")!!)
            .removeEventListener(voiceCallRequestsChildEventListener!!)

        voiceCallRequestsChildEventListener = null
        voiceCallObserver = null
    }


    /**
     * update DB with response or request and status,
     * type could be TYPE_REQUEST or TYPE_RESPONSE
     * and status could be  REQUEST_ACCEPTED, REQUEST_DECLINED or REQUEST_PENDING*/
    fun processVoiceCall(type:String,
                         firebaseUID:String,
                         status:String? = null,
                         request: VoiceCallRequest? = null){
        processVoiceCallAsync(type = type,
            firebaseUID = firebaseUID,
            status = status,
            request = request)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { _ -> }
    }

    fun processVoiceCallAsync(type:String,
                              firebaseUID:String,
                              status:String? = null,
                              request: VoiceCallRequest? = null):Observable<Outcome> =
        Observable.create { emitter ->
            var childNodeUpdate:HashMap<String, Any> = hashMapOf()
            when(type){
                TYPE_REQUEST ->{
                    childNodeUpdate = hashMapOf(
                        "/voice_call_requests/$firebaseUID/from/${dataManager.getUserDetailsParam<String>("firebaseUID")}" to request!!,
                        "/voice_call_requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/to/$firebaseUID" to request)
                }
                TYPE_RESPONSE ->{
                    childNodeUpdate = hashMapOf(
                        "/voice_call_requests/${dataManager.getUserDetailsParam<String>("firebaseUID")}/from/$firebaseUID/status" to status!!,
                        "/voice_call_requests/$firebaseUID/to/${dataManager.getUserDetailsParam<String>("firebaseUID")}/status" to status)
                }
            }

            firebaseDatabase
                .reference
                .updateChildren(childNodeUpdate)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val r:Outcome = Outcome.SUCCESS<Nothing>()
                        Timber.e( "processVoiceCall: voice call status updated successfully in DB")
                        emitter.onNextAndComplete(r)
                    } else {
                        val r:Outcome = Outcome.FAILURE<Nothing>()
                        Timber.e( "processVoiceCall: voice call status updated with issues in DB")
                        emitter.onNextAndComplete(r)
                    }
                }
        }


    fun sendVoiceCallRequestViaFCM(request: FBUserDetailsVModel):Observable<Outcome> =
        Observable.create { emitter ->
            val data: VoiceCallData = VoiceCallData.from(request)
            val fcMessage: FirebaseCloudMessage =
                FirebaseCloudMessage(request.firebaseUID, data)

            fcmClientApi
                .sendCloudMessage(fcMessage)
                .enqueue(
                    object: Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val r:Outcome = Outcome.SUCCESS<Nothing>()
                            emitter.onNextAndComplete(r)
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            val r:Outcome = Outcome.FAILURE<Nothing>(reason = t.message)
                            emitter.onNextAndComplete(r)
                        }

                    })
        }
}