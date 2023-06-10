package app.slyworks.voice_call_feature

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.base_feature.VibrationManager
import app.slyworks.communication_lib.CallManager
import app.slyworks.constants_lib.REQUEST_PENDING
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.VoiceCallRequest
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 8:50 PM, 11/08/2022.
 */
class VoiceCallViewModel
    @Inject
    constructor(private val callManager: CallManager,
                private val vibrationManager: VibrationManager,
                private val dataManager: DataManager) : ViewModel() {

    //region Vars
    val voiceCallStartedLiveData:MutableLiveData<Boolean> =
        MutableLiveData()
    val voiceCallAcceptedLiveData:MutableLiveData<Boolean> =
        MutableLiveData()
    val voiceCallDeclinedLiveData:MutableLiveData<Boolean> =
        MutableLiveData()
    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    fun vibrate(type:Int) = vibrationManager.vibrate(type)

    fun stopVibration() = vibrationManager.stopVibration()

    fun getUserDetailsUtils():FBUserDetailsVModel =
        dataManager.getUserDetailsParam<FBUserDetailsVModel>()!!

    fun sendVoiceCallRequestViaFCM(userDetails: FBUserDetailsVModel){
        disposables +=
        callManager.sendVoiceCallRequestViaFCM(userDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({

            },{})
    }

    fun processVoiceCall(type:String,
                         firebaseUID:String,
                         status:String? = null){
        val request: VoiceCallRequest =
            VoiceCallRequest(getUserDetailsUtils(), REQUEST_PENDING)

        callManager.processVoiceCallAsync(
                type = type,
                firebaseUID = firebaseUID,
                status = status,
                request = request)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({

            },{})
    }
}