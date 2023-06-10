package app.slyworks.room_lib.daos

import androidx.room.*
import app.slyworks.constants_lib.NOT_SENT
import app.slyworks.room_lib.room_models.Message
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow


/**
 *Created by Joshua Sylvanus, 8:30 PM, 1/19/2022.
 */
@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMessage(vararg messages: Message)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateMessage(vararg messages: Message):Int

    @Delete
    fun deleteMessage(vararg messages: Message):Int

    @Query("SELECT * FROM Message ORDER BY time_stamp ASC")
    fun observeMessages(): Flowable<MutableList<Message>>

    @Query("SELECT * FROM Message where from_uid == :firebaseUID  OR to_uid == :firebaseUID")
    fun observeMessagesForUID(firebaseUID:String):Flowable<MutableList<Message>>

    @Query("SELECT * FROM Message")
    fun getMessages():MutableList<Message>

    @Query("SELECT * FROM Message where message_id == :messageID LIMIT 1")
    fun getMessageByID(messageID:String): Message

    @Query("SELECT * FROM Message where from_uid == :firebaseUID  OR to_uid == :firebaseUID")
    fun getMessagesForUID(firebaseUID:String):MutableList<Message>

    @Query("SELECT COUNT(*) FROM Message")
    fun getMessageCount():Long

    @Query("SELECT COUNT(*) FROM Message where from_uid == :firebaseUID OR to_uid == :firebaseUID")
    fun getMessageCountForUID(firebaseUID:String):Long

    @Query("SELECT * FROM Message where status == $NOT_SENT")
    fun getUnsentMessages():MutableList<Message>

}