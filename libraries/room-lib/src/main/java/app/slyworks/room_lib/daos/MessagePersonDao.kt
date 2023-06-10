package app.slyworks.room_lib.daos

import androidx.room.*
import app.slyworks.room_lib.room_models.MessagePerson
import kotlinx.coroutines.flow.Flow


/**
 * Created by Joshua Sylvanus, 8:59 PM, 1/19/2022.
 */
@Dao
interface MessagePersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessagePerson(messagePerson: MessagePerson)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessagePerson(messagePersons: MessagePerson)

    @Delete
    suspend fun deleteMessagePerson(vararg messagePersons: MessagePerson)

    @Query("SELECT * FROM MessagePerson")
    fun observeMessagePersons(): Flow<MutableList<MessagePerson>>

    @Query("SELECT * FROM MessagePerson")
    suspend fun getMessagePersons():MutableList<MessagePerson>

    @Query("SELECT * FROM MessagePerson where firebase_uid == :firebaseUID LIMIT 1")
    suspend fun getMessagePersonByID(firebaseUID:String): MessagePerson
}