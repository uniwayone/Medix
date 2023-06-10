package app.slyworks.communication_lib

import androidx.annotation.VisibleForTesting
import app.slyworks.constants_lib.*
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.data_lib.models.PersonVModel
import app.slyworks.data_lib.toCloudMessage
import app.slyworks.fcm_api_lib.FCMClientApi
import app.slyworks.fcm_api_lib.FirebaseCloudMessage
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.firebase_commons_lib.MValueEventListener
import app.slyworks.models_commons_lib.models.MessageCloudMessage
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.utils.onNextAndComplete
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.reflect.Modifier
import java.util.concurrent.TimeUnit


/**
 * Created by Joshua Sylvanus, 8:24 PM, 18/11/2022.
 */
class MessageManager(
    private val firebaseDatabase: FirebaseDatabase,
    private val fcmClientApi: FCMClientApi,
    private val firebaseUtils: FirebaseUtils,
    private val dataManager: DataManager) {

    private val disposables = CompositeDisposable()

    private fun parseMessagesAndPersons(){}

    fun getPersonsAndMessages(): Observable<Outcome> =
        getMessagesFromFB()
            .map(::mapSenderUIDToMessages)
            .map(::mapSenderUIDToPersons)
            .flatMap { savePersonsToDB(it) }
            .flatMap { saveMessagesToDB(it.getTypedValue()) }

    private fun getMessagesFromFB():Observable<List<MessageVModel>> =
        Observable.create { emitter ->
            val l:MutableList<MessageVModel> = mutableListOf()

            firebaseDatabase.reference
                .child("messages")
                .child(dataManager.getUserDetailsParam("firebaseUID")!!)
                .get()
                .addOnCompleteListener {
                   if(it.isSuccessful){
                       for(child in it.result!!.children)
                           l.add(child.getValue(MessageVModel::class.java)!!)

                       emitter.onNextAndComplete(l)
                   }else{
                       Timber.e("error occurred:${it.exception?.message}")
                       emitter.onNextAndComplete(emptyList())
                   }
                }
        }

    private fun mapSenderUIDToMessages(messageList:List<MessageVModel>):Map<String, MutableList<MessageVModel>>{
        val senderToMessagesMap:MutableMap<String, MutableList<MessageVModel>> = mutableMapOf()

        /* very inefficient loop in the order of O(nm)*/
        messageList.forEach { message: MessageVModel ->
            val UID:String
            if(message.type == OUTGOING_MESSAGE)
                UID = message.fromUID
            else
                UID = message.toUID

            if(senderToMessagesMap.containsKey(UID).not())
                senderToMessagesMap.put(UID, mutableListOf())

            senderToMessagesMap.get(UID)!!.add(message)
        }

        return senderToMessagesMap
    }

    private fun mapSenderUIDToPersons(UIDToMessagesListMap: Map<String, MutableList<MessageVModel>>)
    : Map<PersonVModel, List<MessageVModel>> {
        val map:MutableMap<PersonVModel, List<MessageVModel>> = mutableMapOf()

        for(i in UIDToMessagesListMap.keys){
            val name:String
            val lastMessage: MessageVModel
            var unreadMessageCount:Int = 0

            val l:MutableList<MessageVModel> = UIDToMessagesListMap.get(i)!!
            l.sort()
            l.forEach {
                if(it.status != READ)
                    ++unreadMessageCount
            }

            lastMessage = l.last()
            if(lastMessage.type == OUTGOING_MESSAGE)
                name = lastMessage.senderFullName
            else
                name = lastMessage.receiverFullName


            val person: PersonVModel = parsePerson(lastMessage, i, name, unreadMessageCount)
            map.put(person, l)
        }

        return map
    }

    private fun parsePerson(m: MessageVModel,
                            UID:String,
                            name:String,
                            unreadMessageCount:Int): PersonVModel
            = PersonVModel(
        firebaseUID = UID,
        userAccountType = m.accountType,
        lastMessageType = m.type,
        lastMessageContent = m.content,
        lastMessageStatus = m.status,
        lastMessageTimeStamp = m.timeStamp,
        imageUri = m.receiverImageUri,
        fullName = name,
        unreadMessageCount = unreadMessageCount,
        FCMRegistrationToken = m.FCMRegistrationToken)


    private fun savePersonsToDB(UIDToMessagesListMap:Map<PersonVModel, List<MessageVModel>>)
            :Observable<Outcome> =
        Observable.create { emitter ->
                var outcome = Outcome.SUCCESS(UIDToMessagesListMap)
                try {
                    dataManager.addPersons(*UIDToMessagesListMap.keys.toTypedArray())
                } catch (e: Exception) {
                    Timber.e("error occurred:${e.message}")
                    outcome = Outcome.FAILURE(value = UIDToMessagesListMap, reason = e.message)
                } finally {
                    emitter.onNextAndComplete(outcome)
                }
        }

    private fun saveMessagesToDB(UIDToMessagesListMap: Map<PersonVModel, List<MessageVModel>>)
    :Observable<Outcome> =
        Observable.create { emitter ->
                var outcome = Outcome.SUCCESS(UIDToMessagesListMap)
                try {
                    UIDToMessagesListMap.values.forEach {
                      dataManager.addMessages(*it.toTypedArray())
                    }
                } catch (e: Exception) {
                    Timber.e("error occurred:${e.message}")
                    outcome = Outcome.FAILURE(value = UIDToMessagesListMap, reason = e.message)
                } finally {
                    emitter.onNextAndComplete(outcome)
                }
        }

    fun sendMessage(message: MessageVModel):Observable<MessageVModel> =
       getMessageReceiverFCMRegToken(message.toUID)
            .flatMap { sendCloudMessage(message, it) }
            .retryWhen { attempt:Observable<Throwable> ->
               attempt
                   .zipWith(Observable.range(1,3),{time, index -> index})
                   .concatMap {index:Int ->
                       Observable.timer(100L * index, TimeUnit.MILLISECONDS)
                   }
            }
            .flatMap { sendDBCloudMessage(message) }
           .retryWhen { attempt:Observable<Throwable> ->
               attempt
                   .zipWith(Observable.range(1,3),{time, index -> index})
                   .concatMap {index:Int ->
                       Observable.timer(100L * index, TimeUnit.MILLISECONDS)
                   }
           }
           .flatMap { saveMessageToDB(it) }

    private fun getMessageReceiverFCMRegToken(UID:String):Observable<String> =
        Observable.create { emitter ->
            firebaseUtils
                 .getFCMRegistrationTokenRef(UID)
                 .get()
                 .addOnCompleteListener {
                     if(it.isSuccessful){
                         emitter.onNextAndComplete(it.getResult()!!.value as String)
                     }else
                         emitter.onError(Exception("unable to get user's FCM registration token"))
                 }
        }

    private fun sendCloudMessage(message: MessageVModel, receiverFcmRegToken:String):Observable<MessageVModel> =
        Observable.create { emitter ->
            val data: MessageCloudMessage = message.toCloudMessage()
            fcmClientApi.sendCloudMessage(FirebaseCloudMessage(to = receiverFcmRegToken, data = data))
                .enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if(response.isSuccessful)
                            message.status = DELIVERED
                        else
                            message.status = NOT_SENT

                        emitter.onNextAndComplete(message)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Timber.e("error occurred: ${t.message}")

                        message.status = NOT_SENT
                        emitter.onError(Exception("cloud message not sent successfully"))
                    }
                })
        }

    private fun sendDBCloudMessage(message: MessageVModel):Observable<MessageVModel>{
        /* creating 2 copies of the messages to keep sender and receiver version different */
        message.status = DELIVERED
        val message2: MessageVModel = message.copy()

        /* if the sender's DB message node has other messages, just add a new empty node */
        val senderMessageNodeKey: String? = firebaseDatabase
            .reference.child("messages/${message.fromUID}")
            .push().key

        /* if the receiver's DB message node has other messages, just add a new empty node*/
        val receiverMessageNodeKey: String? = firebaseDatabase
            .reference.child("messages/${message.toUID}")
            .push().key

        return getChildNodesObservable( message,
            message2,
            senderMessageNodeKey,
            receiverMessageNodeKey)
            .flatMap {
                if (it.isSuccess) {
                    Observable.just(message)
                } else {
                    Observable.error<MessageVModel>(Exception("message not successfully sent to FB"))
                    //Observable.just(message.apply { status = NOT_SENT })
                }
            }
    }

    private fun getChildNodesObservable(message: MessageVModel,
                                        message2: MessageVModel,
                                        senderMessageNodeKey:String?,
                                        receiverMessageNodeKey: String?):Observable<Outcome> =
        /*
       * Observable for case when both receiver and sender message nodes are NOT
       * empty,hence perform a simultaneous child node update */
        Observable.create<Outcome>{ emitter ->
            val childNode: HashMap<String, Any> = hashMapOf(
                "/messages/${message.fromUID}/$senderMessageNodeKey" to message,
                "/messages/${message.toUID}/$receiverMessageNodeKey" to message2)

            firebaseDatabase
                .reference
                .updateChildren(childNode)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onNextAndComplete(Outcome.SUCCESS(null))
                    else
                        emitter.onNextAndComplete(Outcome.FAILURE(null))
                }
        }

    private fun saveMessageToDB(message: MessageVModel):Observable<MessageVModel> =
        Observable.fromCallable {
           dataManager.addMessages(message)
           message
        }

    @VisibleForTesting(otherwise = Modifier.PRIVATE)
    internal fun addMessagesToDB(vararg message: MessageVModel):Observable<Outcome> =
        Observable.create { emitter ->
            var o:Outcome = Outcome.SUCCESS<Unit>(null)
                try {
                    dataManager.addMessages(*message)
                }catch (e:Exception){
                    Timber.e("error occurred:${e.message}")
                    o = Outcome.FAILURE<Unit>(null, reason = e.message)
                }finally {
                    emitter.onNextAndComplete(o)
                }
        }

    fun observeUserTypingStatus(uid:String):Observable<Boolean> =
        Observable.create { emitter ->
            val myUID:String = dataManager.getUserDetailsParam<String>("firebaseUID")!!
            firebaseUtils.getIsUserTypingRef(myUID, uid)
                .addValueEventListener(
                    MValueEventListener(onDataChangeFunc = {
                        emitter.onNext(it.getValue(Boolean::class.java)!!)
                    }))
        }

    fun updateUserTypingStatus(status:Boolean, UID:String){
        Completable.create { emitter ->
            val myUID:String = dataManager.getUserDetailsParam<String>("firebaseUID")!!
            firebaseUtils.getIsUserTypingRef(myUID, UID)
                .setValue(status)
                .addOnCompleteListener {
                    emitter.onComplete()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()
    }

}