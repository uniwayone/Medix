package app.slyworks.communication_lib

import app.slyworks.data_lib.models.CallHistoryVModel
import app.slyworks.data_lib.DataManager
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.utils.onNextAndComplete
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Job


/**
 * Created by Joshua Sylvanus, 4:27 PM, 06/07/2022.
 */
class CallHistoryManager(
    private val firebaseFS: FirebaseDatabase,
    private val dataManager: DataManager) {
    //region Vars
    private var currentVideoCall: CallHistoryVModel? = null
    private var currentVideoCallStartTime:Long? = null
    private var currentVideoCallEndTime:Long? = null

    private var currentVoiceCall: CallHistoryVModel? = null
    private var currentVoiceCallStartTime:Long? = null
    private var currentVoiceCallEndTime:Long? = null

    private var observeCallsHistory: Job? = null

    //endregion

    private fun getInitialSingleSource():Single<MutableList<CallHistoryVModel>> =
        if(dataManager.getCallHistoryCount() == 0L)
              Single.just(emptyList<CallHistoryVModel>() as MutableList<CallHistoryVModel>)
        else
            Single.just(dataManager.getCallHistory())

    fun observeCallsHistory(): Observable<List<CallHistoryVModel>> =
        Observable.create { emitter ->
                   dataManager
                       .observeCallHistory()
                       .startWith(getInitialSingleSource())
                       .distinctUntilChanged()
                       .subscribe {
                            emitter.onNext(it)
                        }
        }

    fun detachObserveCallsHistoryObserver(){
        observeCallsHistory!!.cancel()
        observeCallsHistory = null
    }

    private fun saveCallHistory(callHistory: CallHistoryVModel) {
        Observable.just(saveCallHistoryToDB(callHistory))
            .flatMap{ _ -> saveCallHistoryToFBObservable(callHistory) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()
    }

    private fun saveCallHistoryToDB(callHistory: CallHistoryVModel):Unit =
           dataManager.addCallHistory(callHistory)

    private fun saveCallHistoryToFBObservable(callHistory: CallHistoryVModel): Observable<Outcome> =
        Observable.create { emitter ->
            val key:String = FirebaseDatabase.getInstance()
                .reference
                .child("/call_history/${dataManager.getUserDetailsParam<String>("firebaseUID")}")
                .push()
                .key!!

            firebaseFS
                .reference
                .child("/call_history/${dataManager.getUserDetailsParam<String>("firebaseUID")}")
                .child(key)
                .setValue(callHistory)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        emitter.onNextAndComplete(Outcome.SUCCESS<Nothing>())
                    }else
                        emitter.onNextAndComplete(Outcome.FAILURE(value = it.exception?.message))
                }
        }

    fun onVideoCallStarted(callHistory: CallHistoryVModel){
        currentVideoCallStartTime = System.currentTimeMillis()
        currentVideoCall = callHistory
    }

    fun onVideoCallStopped(){
        currentVideoCallEndTime = System.currentTimeMillis()

        val duration:Long = currentVideoCallEndTime!! - currentVideoCallStartTime!!
        currentVideoCall!!.duration = duration.toString()

        saveCallHistory(currentVideoCall!!)
    }

    fun onVoiceCallStarted(callHistory: CallHistoryVModel){
        currentVoiceCallStartTime = System.currentTimeMillis()
        currentVoiceCall = callHistory
    }

    fun onVoiceCallStopped(){
        currentVoiceCallEndTime = System.currentTimeMillis()

        val duration:Long = currentVoiceCallEndTime!! - currentVoiceCallStartTime!!
        currentVoiceCall!!.duration = duration.toString()

        saveCallHistory(currentVoiceCall!!)
    }
}