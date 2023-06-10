package app.slyworks.core_feature.view_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.communication_lib.ConsultationRequestsManager
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.ConsultationRequestVModel
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.models_commons_lib.models.MessageMode
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by Joshua Sylvanus, 9:21 AM, 27/06/2022.
 */
class ViewProfileFragmentViewModel
    @Inject
    constructor(private val consultationRequestsManager: ConsultationRequestsManager,
                private val dataManager: DataManager) : ViewModel(){
    //region Vars
    private val _consultationRequestStatusLiveData:MutableLiveData<String> = MutableLiveData()
    val consultationRequestStatusLiveData:LiveData<String>
    get() = _consultationRequestStatusLiveData

    private val disposables = CompositeDisposable()

    private lateinit var userUID:String
    //endregion

    fun getUserDetailsUser():FBUserDetailsVModel = dataManager.getUserDetailsParam<FBUserDetailsVModel>()!!

    fun observeConsultationRequestStatus(userUID:String){
        this.userUID = userUID
        disposables +=
        consultationRequestsManager.observeConsultationRequestStatus(userUID)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
               _consultationRequestStatusLiveData.postValue(it)
            }
    }

    fun sendConsultationRequest(request:ConsultationRequestVModel, mode: MessageMode = MessageMode.DB_MESSAGE)
        = consultationRequestsManager.sendConsultationRequest(request, mode)

    override fun onCleared() {
        disposables.clear()
        consultationRequestsManager.detachCheckRequestStatusListener(userUID)
        super.onCleared()
    }

}