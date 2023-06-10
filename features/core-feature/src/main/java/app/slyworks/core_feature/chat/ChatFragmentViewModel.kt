package app.slyworks.core_feature.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.communication_lib.MessageManager
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.data_lib.models.PersonVModel
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 11:27 AM, 1/18/2022.
 */
class ChatFragmentViewModel
    @Inject
    constructor(private val networkRegister: NetworkRegister,
                private val messageManager: MessageManager,
                private val dataManager: DataManager) : ViewModel() {

    //region Vars
    private val disposables: CompositeDisposable = CompositeDisposable()

    private val _successStateLiveData: MutableLiveData<Map<PersonVModel, MutableList<MessageVModel>>> = MutableLiveData()
    val successStateLiveData: LiveData<Map<PersonVModel, MutableList<MessageVModel>>>
        get() = _successStateLiveData

    private val _introStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val intoStateLiveData: LiveData<Boolean>
        get() = _introStateLiveData

    private val _errorStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val errorStateLiveData: LiveData<Boolean>
        get() = _errorStateLiveData

    private val _errorDataLiveData: MutableLiveData<String> = MutableLiveData()
    val errorDataLiveData: LiveData<String>
        get() = _errorDataLiveData

    private val _progressStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val progressStateLiveData: LiveData<Boolean>
        get() = _progressStateLiveData
    //endregion

    fun getUserAccountType():String = dataManager.getUserDetailsParam<String>("accountType")!!

    fun getChats() {
        disposables +=
        networkRegister.subscribeToNetworkUpdates()
            .flatMap {
                if (!it)
                    Observable.just(Outcome.ERROR<Unit>(additionalInfo = "no network connection"))
                else
                    messageManager.getPersonsAndMessages()
                    //messageManager.observeMessagePersons()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                _progressStateLiveData.postValue(false)

                when {
                    it.isSuccess -> {
                        _introStateLiveData.postValue(false)
                        _errorStateLiveData.postValue(false)
                        _successStateLiveData.postValue(it.getTypedValue<Map<PersonVModel, MutableList<MessageVModel>>>())
                    }
                    it.isFailure -> {
                        _errorStateLiveData.postValue(false)
                        _introStateLiveData.postValue(true)
                    }
                    it.isError -> {
                        _introStateLiveData.postValue(false)
                        _errorDataLiveData.postValue(it.getAdditionalInfo())
                        _errorStateLiveData.postValue(true)
                    }
                }
            }
    }

    fun getNetworkStatus(): Boolean = networkRegister.getNetworkStatus()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()

        //messageManager.detachObserveMessagePersonsListener()
        networkRegister.unsubscribeToNetworkUpdates()
    }
}
