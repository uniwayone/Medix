package app.slyworks.auth_feature.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth_lib.LoginManager
import app.slyworks.auth_lib.RegistrationManager
import app.slyworks.auth_lib.VerificationDetails
import app.slyworks.base_feature.VibrationManager
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 6:39 AM, 07/06/2022.
 */
class LoginActivityViewModel
    @Inject
    constructor(private val networkRegister: NetworkRegister,
                private val loginManager: LoginManager,
                private val vibrationManager: VibrationManager)
    : ViewModel(){
    //region Vars
    private val _passwordResetStatus:MutableLiveData<Boolean> = MutableLiveData()
    val passwordResetLiveData:LiveData<Boolean>
    get() = _passwordResetStatus as LiveData<Boolean>

    private val _progressStateLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val progressStateLiveData:LiveData<Boolean>
    get() = _progressStateLiveData

    private val _loginSuccessLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val loginSuccessLiveData:LiveData<Boolean>
    get() = _loginSuccessLiveData as LiveData<Boolean>

    private val _resetPasswordFailureDataLiveData:MutableLiveData<String> = MutableLiveData()
    val resetPasswordFailureDataLiveData:LiveData<String>
        get() = _resetPasswordFailureDataLiveData as LiveData<String>

    private val _resetPasswordFailedLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val resetPasswordFailedLiveData:LiveData<Boolean>
    get() = _resetPasswordFailedLiveData as LiveData<Boolean>

    private val _messageLiveData:MutableLiveData<String> = MutableLiveData()
    val messageLiveData:LiveData<String>
    get() = _messageLiveData

    private val _accountNotVerifiedLD:MutableLiveData<Boolean> = MutableLiveData()
    val accountNotVerifiedLD:LiveData<Boolean>
    get() = _accountNotVerifiedLD

    private val _forgotPasswordLoadingLD:MutableLiveData<Boolean> = MutableLiveData()
    val forgotPasswordLoadingLD:LiveData<Boolean>
    get() = _forgotPasswordLoadingLD

    var emailVal:String = ""
    var passwordVal:String = ""

    private val disposables:CompositeDisposable = CompositeDisposable()
    private var disposables2:Disposable = Disposable.empty()
    //endregion

    fun vibrate(type:Int) = vibrationManager.vibrate(type)

    fun getNetworkStatus() = networkRegister.getNetworkStatus()

    fun subscribeToNetwork():LiveData<Boolean>{
        val l:MutableLiveData<Boolean> = MutableLiveData()

        disposables2 = networkRegister
            .subscribeToNetworkUpdates()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                l.postValue(it)
            }

        return l
    }

    fun unsubscribeToNetwork(){
        networkRegister.unsubscribeToNetworkUpdates()
        disposables2.dispose()
    }

    fun login(email:String, password:String){
        if(!networkRegister.getNetworkStatus()){
            _messageLiveData.postValue("Please check your connection and try again")
            return
        }

        disposables +=
        loginManager
            .loginUser(email, password)
            .doOnSubscribe { _progressStateLiveData.postValue(true)  }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { it: Outcome ->
                _progressStateLiveData.postValue(false)

                when{
                    it.isSuccess -> _loginSuccessLiveData.postValue(true)
                    it.isFailure -> _messageLiveData.postValue(it.getAdditionalInfo())
                    it.isError -> _accountNotVerifiedLD.postValue(true)
                }
            }
    }

    fun handleForgotPassword(email: String){
        if(!networkRegister.getNetworkStatus()){
            _messageLiveData.postValue("Please check your connection and try again")
            return
        }

        disposables +=
        loginManager
            .handleForgotPassword(email)
            .doOnSubscribe { _forgotPasswordLoadingLD.postValue(true) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { it:Boolean ->
                _progressStateLiveData.postValue(false)
                _passwordResetStatus.postValue(it)
            }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}