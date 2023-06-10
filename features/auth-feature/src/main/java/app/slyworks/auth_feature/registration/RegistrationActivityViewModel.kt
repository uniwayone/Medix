package app.slyworks.auth_feature.registration

import android.app.Activity
import android.net.Uri
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_lib.OTPVerificationStage
import app.slyworks.auth_lib.RegistrationManager
import app.slyworks.auth_lib.VerificationHelper
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.models_commons_lib.models.TempUserDetails
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class RegistrationActivityViewModel
    @Inject
    constructor(private val networkRegister: NetworkRegister,
                private val registrationManager: RegistrationManager,
                private val verificationHelper: VerificationHelper) :  ViewModel() {

    //region Vars
    private var currentTimer:CountDownTimer? = null

    val registrationDetails:TempUserDetails = TempUserDetails()

    private var disposable:Disposable = Disposable.empty()
    private val disposables:CompositeDisposable = CompositeDisposable()

    var inputOTPSubject:PublishSubject<String> = PublishSubject.create()
    var resendOTPSubject:PublishSubject<Boolean> = PublishSubject.create()

    private val networkStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val profileImageUriLiveData:MutableLiveData<Uri> = MutableLiveData()
    val progressLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val messageLiveData:MutableLiveData<String> = MutableLiveData()
    val registrationSuccessfulLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val beginOTPVerificationLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val verificationSuccessfulLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val verificationFailureLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val otpCountDownLD:MutableLiveData<Int> = MutableLiveData(90)
    val otpCountDownFinishedLD:MutableLiveData<Boolean> = MutableLiveData()
    //endregion

    fun subscribeToNetwork(): LiveData<Boolean> {
        disposable = networkRegister
            .subscribeToNetworkUpdates()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                networkStatusLiveData.postValue(it)
            }

        return networkStatusLiveData
    }

    fun unsubscribeToNetwork(){
        networkRegister.unsubscribeToNetworkUpdates()
        disposable.dispose()
    }

    fun handleProfileImageUri(o: Observable<Uri>){
        disposables +=
        o.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                registrationDetails.image_uri_init = it
                it.let{ profileImageUriLiveData.postValue(it) }
            }
    }

    fun registerUser(){
       if(!networkRegister.getNetworkStatus()){
           messageLiveData.postValue("please check your connection and try again")
           return
       }

       disposables +=
       registrationManager.register(registrationDetails)
           .doOnSubscribe { progressLiveData.postValue(true) }
           .subscribeOn(Schedulers.io())
           .observeOn(Schedulers.io())
           .subscribe({
               progressLiveData.postValue(false)

               when{
                   it.isSuccess -> registrationSuccessfulLiveData.postValue(true)
                   it.isFailure -> messageLiveData.postValue(it.getAdditionalInfo())
               }
           },{
               Timber.e("error occurred:${it.message}")
               progressLiveData.postValue(false)
               messageLiveData.postValue(it.message ?: "an error occurred")
           })
    }

    fun verifyByEmail(){
        if(!networkRegister.getNetworkStatus()){
            messageLiveData.postValue("please check your connection and try again")
            return
        }

        disposables +=
        verificationHelper.verifyBySendingEmail()
            .doOnSubscribe { progressLiveData.postValue(true) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { it: Outcome ->
                progressLiveData.postValue(false)

                when{
                    it.isSuccess ->
                        verificationSuccessfulLiveData.postValue(true)
                    it.isFailure ->{
                        verificationFailureLiveData.postValue(true)
                        messageLiveData.postValue(it.getAdditionalInfo())
                    }
                }
            }
    }

    fun verifyViaOTP(phoneNumber:String, a:Activity){
        if(!networkRegister.getNetworkStatus()){
            messageLiveData.postValue("please check your connection and try again")
            return
        }

        inputOTPSubject = verificationHelper.otpSubject
        resendOTPSubject = PublishSubject.create()

        disposables +=
        resendOTPSubject
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { verificationHelper.resendSubject.onNext(phoneNumber) }

        disposables +=
        verificationHelper.verifyViaOTP(phoneNumber, a)
            .doOnSubscribe { progressLiveData.postValue(true) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                progressLiveData.postValue(false)

                when{
                    it.isSuccess -> {
                        when (it.getTypedValue<OTPVerificationStage>()) {
                            OTPVerificationStage.ENTER_OTP ->
                                beginOTPVerificationLiveData.postValue(true)
                            OTPVerificationStage.PROCESSING -> {}
                            OTPVerificationStage.VERIFICATION_SUCCESS ->
                                verificationSuccessfulLiveData.postValue(true)
                            OTPVerificationStage.VERIFICATION_FAILURE -> {
                                messageLiveData.postValue(it.getAdditionalInfo())
                                verificationFailureLiveData.postValue(false)
                            }
                        }
                    }

                    it.isFailure -> {}
                    }
                },{
                    Timber.e("error occurred: ${it.message}")
                    progressLiveData.postValue(false)
                    messageLiveData.postValue(it.message)
                })
    }

    fun initOTPTimeoutCountdown(){ currentTimer = OTPTimeoutCountDownTimer().start() }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        registrationManager.unbind()
        currentTimer?.cancel()
        currentTimer = null
    }

    inner class OTPTimeoutCountDownTimer : CountDownTimer(90_000, 1_000){
        override fun onTick(p0: Long) {
            otpCountDownLD.postValue(otpCountDownLD.value?.minus(1))
        }

        override fun onFinish() {
           otpCountDownFinishedLD.postValue(true)
        }
    }
}
