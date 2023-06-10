package app.slyworks.room_lib.daos

import androidx.room.*
import app.slyworks.room_lib.room_models.Person
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow


/**
 *Created by Joshua Sylvanus, 8:12 AM, 27/04/2022.
 */
@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersons(vararg persons: Person)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePersons(vararg persons: Person):Int

    @Delete
    fun deletePersons(vararg persons: Person):Int

    @Query("SELECT * FROM Person")
    fun getPersons():MutableList<Person>

    @Query("SELECT * FROM Person")
    fun observePersons(): Flowable<MutableList<Person>>

    @Query("SELECT * FROM Person where firebase_uid == :firebaseUID LIMIT 1")
    fun observePerson(firebaseUID: String):Flowable<Person>

    @Query("SELECT * FROM Person where firebase_uid == :firebaseUID LIMIT 1")
    fun getPersonByID(firebaseUID:String): Person

    @Query("SELECT COUNT(*) FROM Message")
    fun getPersonCount():Long
}