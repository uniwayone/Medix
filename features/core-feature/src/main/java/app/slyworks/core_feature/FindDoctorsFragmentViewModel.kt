package app.slyworks.core_feature

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth_lib.UsersManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by Joshua Sylvanus, 4:21 AM, 1/8/2022.
 */
class FindDoctorsFragmentViewModel
    @Inject
    constructor(private val networkRegister: NetworkRegister,
                private val usersManager: UsersManager) : ViewModel() {

    //region Vars
    val doctorsListLiveData:MutableLiveData<MutableList<FBUserDetailsVModel>> = MutableLiveData()
    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    fun getNetworkStatus():Boolean = networkRegister.getNetworkStatus()

    fun getAllDoctors() = usersManager.getAllDoctors()

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

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}