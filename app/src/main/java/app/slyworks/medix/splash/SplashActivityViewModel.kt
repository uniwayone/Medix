package app.slyworks.medix.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth_lib.LoginManager
import app.slyworks.data_lib.DataManager
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 9:32 PM, 16/08/2022.
 */
class SplashActivityViewModel
    @Inject
    constructor(private val loginManager: LoginManager,
                private val dataManager: DataManager): ViewModel() {

        //region Vars
         private val _isSessionValid:MutableLiveData<Boolean> = MutableLiveData()
         val isSessionValid:LiveData<Boolean>
         get() = _isSessionValid

         private val disposables:CompositeDisposable = CompositeDisposable()
        //endregion

        /*
        private fun checkIfSessionIsExpired():Single<Boolean>
         = Single.fromCallable {
            val lastSignInTime: Long = preferenceManager.get(KEY_LAST_SIGN_IN_TIME, System.currentTimeMillis())
            return@fromCallable timeUtils.isWithinTimePeriod(lastSignInTime, 3, TimeUnit.DAYS)
         }
         */

         fun checkLoginSession(){
           disposables +=
           Observable.combineLatest(Observable.just(loginManager.getLoginStatus()),
                                    Observable.just(
                                        dataManager.getUserDetailsParam<String>("firebaseUID") != null
                                    ),
               { isLoggedIn, isUserDetailsAvailable ->
                   if(isLoggedIn && isUserDetailsAvailable)
                         return@combineLatest true

                   return@combineLatest false
                 })
                 .subscribeOn(Schedulers.io())
                 .observeOn(Schedulers.io())
                 .subscribe(_isSessionValid::postValue)
         }

    override fun onCleared() {
        super.onCleared()

        disposables.clear()
    }
}