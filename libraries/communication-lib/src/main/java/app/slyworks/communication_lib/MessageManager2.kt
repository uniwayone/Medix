package app.slyworks.communication_lib

import androidx.annotation.VisibleForTesting
import app.slyworks.constants_lib.DELIVERED
import app.slyworks.constants_lib.NOT_SENT
import app.slyworks.constants_lib.OUTGOING_MESSAGE
import app.slyworks.constants_lib.READ
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.data_lib.models.PersonVModel
import app.slyworks.firebase_commons_lib.MValueEventListener
import com.google.firebase.database.*
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.utils.onNextAndComplete
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.reflect.Modifier.PRIVATE


/**
 * Created by Joshua Sylvanus, 8:33 PM, 1/8/2022.
 */

class MessageManager2(
    private val firebaseDatabase: FirebaseDatabase,
    private val dataManager: DataManager) {

    //region Vars
    private var handleChangedMessagesDisposable: CompositeDisposable = CompositeDisposable()
    private var handleNewMessagesDisposable: CompositeDisposable = CompositeDisposable()

    private var observeNewMessagesForUIDJob: Job? = null
    private var observeNewMessagePersonsJob: Job? = null

    private lateinit var mUIDValueEventListener: ValueEventListener
    private lateinit var mUIDChildEventListener: ValueEventListener
    //endregion

    fun detachMessagesForUIDListener(firebaseUID: String) {
        observeNewMessagesForUIDJob?.cancel()
        handleChangedMessagesDisposable.clear()

        firebaseDatabase
            .reference
            .child("messages/${dataManager.getUserDetailsParam<String>("firebaseUID")}")
            .orderByChild("from_uid")
            .equalTo(firebaseUID)
            .removeEventListener(mUIDValueEventListener)
    }

    fun observeMessagesForUID(firebaseUID: String): Observable<Outcome> =
        Observable.create { emitter ->
            mUIDValueEventListener = MValueEventListener(onDataChangeFunc = ::handleChangedMessages)

            firebaseDatabase
                .reference
                .child("messages/${dataManager.getUserDetailsParam<String>("firebaseUID")}")
                .orderByChild("fromUID")
                .equalTo(firebaseUID)
                .addValueEventListener(mUIDValueEventListener)

            dataManager
                .observeMessagesForUID(firebaseUID)
                .distinctUntilChanged()
                .subscribe {
                    if (it.isNotEmpty()) {
                        it.sort()
                        val r: Outcome = Outcome.SUCCESS<MutableList<MessageVModel>>(it)
                        emitter.onNext(r)
                    } else {
                        val r: Outcome = Outcome.FAILURE<Nothing>(reason = "no messages")
                        emitter.onNext(r)
                    }
                }
        }

    private fun handleChangedMessages(snapshot: DataSnapshot) {
        if (!snapshot.exists())
            return

        val l: MutableList<MessageVModel> = mutableListOf()
        snapshot.children.forEach {
            val m: MessageVModel = it.getValue(MessageVModel::class.java)!!
            l.add(m)
        }

        if (l.isNullOrEmpty())
            return

        handleChangedMessagesDisposable +=
            addMessagesToDB(*l.toTypedArray())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { _ -> }

    }

    fun detachObserveMessagePersonsListener() {
        observeNewMessagePersonsJob?.cancel()

        firebaseDatabase
            .reference
            .child("messages/${dataManager.getUserDetailsParam<String>("firebaseUID")}")
            .removeEventListener(mUIDChildEventListener)
    }

    fun observeMessagePersons(): Observable<Outcome> =
        Observable.create { emitter ->
            mUIDChildEventListener = MValueEventListener(onDataChangeFunc = ::handleNewMessages)

            firebaseDatabase
                .reference
                .child("messages/${dataManager.getUserDetailsParam<String>("firebaseUID")}")
                .addValueEventListener(mUIDChildEventListener)

            dataManager
                .observePersons()
                .distinctUntilChanged()
                .subscribe {
                    if (it.isNotEmpty()) {
                        val r: Outcome = Outcome.SUCCESS<MutableList<PersonVModel>>(it)
                        emitter.onNext(r)
                    } else {
                        val r: Outcome = Outcome.FAILURE<Unit>(reason = "you don't seem to have any messages at the moment")
                        emitter.onNext(r)
                    }
                }
        }

    private fun handleNewMessages(snapshot: DataSnapshot) {
        /*  snapshot should be a list of all the user's messages from the different senders */
        handleNewMessagesDisposable +=
            getMessageListObservable(snapshot)
                .map(::mapUIDToMessageList)
                .map(::mapUIDMapToPersonList)
                .flatMap(::getPersonsObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    private fun getMessageListObservable(snapshot: DataSnapshot): Observable<List<MessageVModel>> =
        Observable.create<List<MessageVModel>> { emitter ->
            val l: MutableList<MessageVModel> = mutableListOf()
            for (child in snapshot.children)
                l.add(child.getValue(MessageVModel::class.java)!!)

            emitter.onNextAndComplete(l)
        }

    private fun getPersonSetObservable(): Observable<MutableSet<PersonVModel>> =
        Observable.create<MutableSet<PersonVModel>> { emitter ->
            val s: MutableSet<PersonVModel> =
                dataManager
                    .getPersons()
                    .toMutableSet()

            emitter.onNextAndComplete(s)
        }

    private fun getPersonsObservable(personList: List<PersonVModel>): Observable<Boolean> =
        Observable.create<Boolean> { emitter ->
            try {
                dataManager
                    .addPersons(*personList.toTypedArray())

                emitter.onNextAndComplete(true)
            } catch (e: Exception) {
                emitter.onNextAndComplete(false)
            }
        }

    private fun mapUIDToMessageList(messageList: List<MessageVModel>): MutableMap<String, MutableList<MessageVModel>> {
        val sm: MutableMap<String, MutableList<MessageVModel>> = mutableMapOf()

        messageList.forEach { m ->
            val key: String =
                if (m.type == OUTGOING_MESSAGE) m.fromUID
                else m.toUID

            if (!sm.containsKey(key))
                sm.put(key, mutableListOf())

            sm.get(key)!!.add(m)
        }

        return sm
    }

    private fun mapUIDMapToPersonList(UIDMap: MutableMap<String, MutableList<MessageVModel>>)
            : List<PersonVModel> {
        var mList: MutableList<MessageVModel>
        var lastMessage: MessageVModel
        var name: String
        var unreadMessageCount: Int = 0

        val personList: MutableList<PersonVModel> = mutableListOf()
        for (i in UIDMap.keys) {
            mList = UIDMap.get(i)!!
            mList.sort()

            lastMessage = mList.last()
            if (lastMessage.type == OUTGOING_MESSAGE)
                name = lastMessage.senderFullName
            else
                name = lastMessage.receiverFullName


            mList.forEach { m: MessageVModel ->
                if (m.status != READ)
                    unreadMessageCount++
            }

            personList.add(parsePerson(lastMessage, i, name, unreadMessageCount))
        }

        return personList
    }

    private fun parsePerson(
        m: MessageVModel,
        UID: String,
        name: String,
        unreadMessageCount: Int
    ): PersonVModel = PersonVModel(
        firebaseUID = UID,
        userAccountType = m.accountType,
        lastMessageType = m.type,
        lastMessageContent = m.content,
        lastMessageStatus = m.status,
        lastMessageTimeStamp = m.timeStamp,
        imageUri = m.receiverImageUri,
        fullName = name,
        unreadMessageCount = unreadMessageCount,
        FCMRegistrationToken = m.FCMRegistrationToken
    )


    fun sendMessage(message: MessageVModel): Observable<MessageVModel> {
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

        return getChildNodesObservable(
            message,
            message2,
            senderMessageNodeKey,
            receiverMessageNodeKey
        )
            .flatMap {
                if (it.isSuccess) {
                    addMessagesToDB(message)
                    Observable.just(message)
                } else {
                    addMessagesToDB(message.apply { status = NOT_SENT })
                    Observable.just(message.apply { status = NOT_SENT })
                }
            }
    }

    private fun getChildNodesObservable(
        message: MessageVModel,
        message2: MessageVModel,
        senderMessageNodeKey: String?,
        receiverMessageNodeKey: String?
    ): Observable<Outcome> =
        /*
       * Observable for case when both receiver and sender message nodes are NOT
       * empty,hence perform a simultaneous child node update */
        Observable.create<Outcome> { emitter ->
            val childNode: HashMap<String, Any> = hashMapOf(
                "/messages/${message.fromUID}/$senderMessageNodeKey" to message,
                "/messages/${message.toUID}/$receiverMessageNodeKey" to message2
            )

            firebaseDatabase
                .reference
                .updateChildren(childNode)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        emitter.onNext(Outcome.SUCCESS(null))
                    else
                        emitter.onNext(Outcome.FAILURE(null))

                    emitter.onComplete()
                }
        }

    @VisibleForTesting(otherwise = PRIVATE)
    internal fun addMessagesToDB(vararg message: MessageVModel): Observable<Outcome> =
        Observable.create { emitter ->
            try {
                dataManager.addMessages(*message)

                emitter.onNextAndComplete(Outcome.SUCCESS<Unit>(null))
            } catch (e: Exception) {
                emitter.onNextAndComplete(Outcome.FAILURE<Unit>(null, reason = e.message))
            }
        }

}