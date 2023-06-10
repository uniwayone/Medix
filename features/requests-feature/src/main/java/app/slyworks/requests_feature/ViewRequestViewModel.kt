package app.slyworks.requests_feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth_lib.LoginManager
import app.slyworks.auth_lib.UsersManager
import app.slyworks.communication_lib.ConsultationRequestsManager
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.models_commons_lib.models.ConsultationResponse
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Joshua Sylvanus, 8:15 PM, 06/07/2022.
 */

class ViewRequestViewModel
    @Inject
    constructor(private val loginManager: LoginManager,
                private val usersManager: UsersManager,
                private val consultationRequestsManager: ConsultationRequestsManager,
                private val dataManager: DataManager) : ViewModel() {
    //region Vars
    private val _successData:MutableLiveData<FBUserDetailsVModel> = MutableLiveData()
    val successData:LiveData<FBUserDetailsVModel>
    get() = _successData

    private val _successState:MutableLiveData<Boolean> = MutableLiveData()
    val successState:LiveData<Boolean>
    get() = _successState

    private val _errorData:MutableLiveData<String> = MutableLiveData()
    val errorData:LiveData<String>
    get() = _errorData

    private val _errorState:MutableLiveData<Boolean> = MutableLiveData()
    val errorState:LiveData<Boolean>
    get() = _errorState

    val progressState:MutableLiveData<Boolean> = MutableLiveData()

    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion
    fun getUserDetailsUtils():FBUserDetailsVModel = dataManager.getUserDetailsParam<FBUserDetailsVModel>()!!

    fun getLoginStatus():Boolean = loginManager.getLoginStatus()

    fun getUserDetails(userUID:String){
       disposables +=
       usersManager.getUserDataForUID(userUID)
                  .subscribeOn(Schedulers.io())
                  .observeOn(Schedulers.io())
                  .subscribe {
                      progressState.postValue(false)

                      when{
                          it.isSuccess ->{
                              _errorState.postValue(false)
                              _successData.postValue(it.getTypedValue())
                              _successState.postValue(true)
                          }
                          it.isFailure ->{
                              _errorData.postValue(it.getAdditionalInfo())
                              _errorState.postValue(true)
                          }
                      }
                  }
    }

    fun respondToRequest(response: ConsultationResponse){
        progressState.setValue(true)

        disposables +=
            consultationRequestsManager.sendConsultationRequestResponse(response)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    progressState.postValue(false)

                    when {
                        it.isSuccess -> {
                            _errorState.postValue(false)
                        }
                        it.isFailure -> {
                            _errorData.postValue(it.getAdditionalInfo())
                            _errorState.postValue(true)
                        }
                    }
                }

    }

    override fun onCleared() {
        super.onCleared()
        onStop()
    }

    private fun onStop(){
        disposables.clear()
    }
}