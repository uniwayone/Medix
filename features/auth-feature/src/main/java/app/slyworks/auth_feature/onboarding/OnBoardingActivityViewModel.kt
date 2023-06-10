package app.slyworks.auth_feature.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 5:54 PM, 18/05/2022.
 */
class OnBoardingActivityViewModel
    @Inject
    constructor(private val networkRegister: NetworkRegister) : ViewModel() {
    //region Vars
    private val l:MutableLiveData<Boolean> = MutableLiveData()
    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    fun subscribeToNetwork(): LiveData<Boolean>{
        disposables +=
        networkRegister
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
        disposables.clear()
    }

    override fun onCleared() {
        super.onCleared()
        unsubscribeToNetwork()
    }
}