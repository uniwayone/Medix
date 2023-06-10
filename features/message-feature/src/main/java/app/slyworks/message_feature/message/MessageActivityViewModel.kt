package app.slyworks.message_feature.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth_lib.PersonsManager
import app.slyworks.auth_lib.UsersManager
import app.slyworks.communication_lib.ConnectionStatusManager
import app.slyworks.communication_lib.MessageManager
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.utils_lib.TimeHelper
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by Joshua Sylvanus, 12:27 PM, 13/1/2022.
 */
class MessageActivityViewModel
    @Inject
    constructor(private val usersManager: UsersManager,
                private val messageManager: MessageManager,
                private val connectionStatusManager: ConnectionStatusManager,
                private val personsManager: PersonsManager,
                private val dataManager: DataManager,
                val timeHelper: TimeHelper) : ViewModel()  {

    //region Vars
    private val messageListLiveData: MutableLiveData<MutableList<MessageVModel>> = MutableLiveData(mutableListOf())
    private val connectionStatusLiveData:MutableLiveData<String> = MutableLiveData()
    private val userDetailsLiveData:MutableLiveData<FBUserDetailsVModel> = MutableLiveData()

    private val _startCallDataLiveData:MutableLiveData<FBUserDetailsVModel> = MutableLiveData()
    val startCallDataLiveData:LiveData<FBUserDetailsVModel>
    get() = _startCallDataLiveData

    private val _startCallStateLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val startCallStateLiveData:LiveData<Boolean>
    get() = _startCallStateLiveData

    val progressLiveData:MutableLiveData<Boolean> = MutableLiveData()
    val statusLiveData:MutableLiveData<String> = MutableLiveData()

    private var UID: String? = null
    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    fun getUserDetailsUtils(): FBUserDetailsVModel = dataManager.getUserDetailsParam<FBUserDetailsVModel>("userDetails")!!

    fun getUserDetails(firebaseUID:String){
            disposables +=
            usersManager.getUserDataForUID(firebaseUID)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressLiveData.postValue(true) }
                .subscribe {
                    progressLiveData.postValue(false)

                    when{
                        it.isSuccess ->{
                            _startCallDataLiveData.postValue(it.getTypedValue())
                            _startCallStateLiveData.postValue(true)
                        }
                        it.isFailure ->{
                            statusLiveData.postValue(it.getAdditionalInfo())
                        }
                    }
                }
    }

    fun sendMessage(message: MessageVModel) {
              disposables +=
              messageManager.sendMessage(message)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    messageListLiveData.value?.add(message)
                    messageListLiveData.postValue(messageListLiveData.value)

                    updatePersonLastMessageTimeStamp(firebaseUID = message.toUID, message.timeStamp)
                }
                .subscribe { it: MessageVModel ->
                    messageListLiveData.value?.last()?.status = it.status
                    messageListLiveData.postValue(messageListLiveData.value)
                }
    }

    private fun updatePersonLastMessageTimeStamp(firebaseUID: String, timeStamp:String) = personsManager.updateLastMessageTimeStamp(firebaseUID, timeStamp)
    fun updatePersonLastMessageInfo(firebaseUID:String) = personsManager.updateLastMessageInfo(firebaseUID)


    fun observeUserDetails(firebaseUID: String):LiveData<FBUserDetailsVModel>{
       disposables +=
           usersManager.observeUserDataForUID(firebaseUID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it == null)
                    return@subscribe

                 usersManager.updateUserInfo(it)
                     .subscribe { _ ->}
                 userDetailsLiveData.value = it
            }


        return userDetailsLiveData
    }

    fun observeConnectionStatus(firebaseUID:String):LiveData<String>{
        val d:Disposable =
            connectionStatusManager.observeConnectionStatusForUID(firebaseUID.also { UID = it })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe{
                    if(it.status)
                       connectionStatusLiveData.postValue("online")
                    else{
                       val time = timeHelper.convertTimeToString(it.timestamp.toString())
                       connectionStatusLiveData.postValue(time)
                    }
                }

        disposables.add(d)

       return connectionStatusLiveData
    }

    fun observeMessagesForUID(firebaseUID: String) : LiveData<MutableList<MessageVModel>>{
       /* disposables +=
            messageManager.observeMessagesForUID(firebaseUID.also { UID = it })
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                progressLiveData.postValue(false)

                when {
                    it.isSuccess -> {
                        val r: MutableList<MessageVModel> = it.getValue() as MutableList<MessageVModel>
                        messageListLiveData.postValue(r)
                    }
                    it.isFailure ->{
                        statusLiveData.postValue(it.getAdditionalInfo())
                    }
                }
            }*/

        return messageListLiveData
    }

    fun updateUserTypingStatus(status:Boolean){
       messageManager.getPersonsAndMessages()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        connectionStatusManager.detachObserveConnectionStatusForUIDListener(UID!!)
        //messageManager.detachMessagesForUIDListener(UID!!)
        //UsersManager.detachUserDataListener(mUID!!)
    }
}