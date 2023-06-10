package app.slyworks.video_call_feature

import androidx.lifecycle.ViewModel
import app.slyworks.base_feature.VibrationManager
import app.slyworks.communication_lib.CallHistoryManager
import app.slyworks.communication_lib.CallManager
import app.slyworks.data_lib.models.CallHistoryVModel
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.VideoCallRequest
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 5:30 PM, 1/23/2022.
 */
class VideoCallViewModel
    @Inject
    constructor(private val callManager: CallManager,
                private val vibrationManager: VibrationManager,
                private val callHistoryManager: CallHistoryManager,
                private val dataManager: DataManager) : ViewModel(){


    fun vibrate(type:Int) = vibrationManager.vibrate(type)

    fun getUserDetailsUtils(): FBUserDetailsVModel = dataManager.getUserDetailsParam<FBUserDetailsVModel>("userDetails")!!

   fun processVideoCall(type:String, firebaseUID:String, request: VideoCallRequest? = null, status:String? = null){
       callManager.processVideoCall(
           type = type,
           firebaseUID = firebaseUID,
           request = request,
           status = status)
   }

    fun onVideoCallStarted(callHistory: CallHistoryVModel) = callHistoryManager.onVideoCallStarted(callHistory)
    fun onVideoCallStopped() = callHistoryManager.onVideoCallStopped()
}