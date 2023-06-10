package app.slyworks.core_feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth_lib.UsersManager
import app.slyworks.communication_lib.ConsultationRequestsManager
import app.slyworks.constants_lib.EVENT_GET_DOCTOR_USERS
import app.slyworks.constants_lib.EVENT_SEND_REQUEST
import app.slyworks.controller_lib.AppController
import app.slyworks.controller_lib.Observer
import app.slyworks.controller_lib.Subscription
import app.slyworks.controller_lib.clearAndRemove
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.ConsultationRequestVModel
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.models_commons_lib.models.MessageMode
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Joshua Sylvanus, 4:21 AM, 1/8/2022.
 */

class ProfileHostFragmentViewModel
  @Inject
  constructor(private val networkRegister: NetworkRegister,
              private val usersManager: UsersManager,
              private val consultationRequestsManager: ConsultationRequestsManager,
              private val dataManager: DataManager) : ViewModel(), Observer {

   //region Vars
    val progressLD:MutableLiveData<Boolean> = MutableLiveData()
    val messageLD:MutableLiveData<String> = MutableLiveData()
    val doctorsListLiveData: MutableLiveData<MutableList<FBUserDetailsVModel>> = MutableLiveData()

    private val _consultationRequestStatusLiveData:MutableLiveData<String> = MutableLiveData()
    val consultationRequestStatusLiveData: LiveData<String>
        get() = _consultationRequestStatusLiveData

    private lateinit var userUID:String

    private val subscriptions:MutableList<Subscription> = mutableListOf()
    private val disposables: CompositeDisposable = CompositeDisposable()
    //endregion

    init {
        val s: Subscription = AppController.subscribeTo(EVENT_GET_DOCTOR_USERS, this)
        val s2: Subscription = AppController.subscribeTo(EVENT_SEND_REQUEST, this)
        subscriptions.add(s)
    }
    fun getNetworkStatus():Boolean = networkRegister.getNetworkStatus()

    fun getUserDetailsUser():FBUserDetailsVModel = dataManager.getUserDetailsParam<FBUserDetailsVModel>()!!

    fun observeConsultationRequestStatus(userUID:String){
        this.userUID = userUID
        disposables +=
            consultationRequestsManager.observeConsultationRequestStatus(userUID)
                .doOnSubscribe { progressLD.postValue(true) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    progressLD.postValue(false)

                    _consultationRequestStatusLiveData.postValue(it)
                }
    }

    fun sendConsultationRequest(request: ConsultationRequestVModel, mode: MessageMode = MessageMode.DB_MESSAGE){
        progressLD.setValue(true)
        consultationRequestsManager.sendConsultationRequest(request, mode)
    }


    fun getAllDoctors(){
        if(!getNetworkStatus()){
            messageLD.postValue("no network connection")
        }

        progressLD.postValue(true)
        usersManager.getAllDoctors()
    }

    private fun observeDoctors(){
        disposables +=
        usersManager.observeDoctors()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it.isSuccess)
                    doctorsListLiveData.postValue(it.getTypedValue())
            },{})
    }

    override fun <T> notify(event: String, data: T?) {
       when(event){
           EVENT_GET_DOCTOR_USERS ->{
               progressLD.postValue(false)

               data as Outcome
               if(data.isSuccess) doctorsListLiveData.postValue(data.getTypedValue())
               else messageLD.postValue(data.getAdditionalInfo())

           }
           EVENT_SEND_REQUEST ->{
               progressLD.postValue(false)
               messageLD.postValue("your request was successfully sent")
           }
       }
    }

    override fun onCleared() {
        super.onCleared()
        consultationRequestsManager.detachCheckRequestStatusListener(userUID)
        disposables.clear()
        subscriptions.forEach { it.clearAndRemove() }
    }
}
