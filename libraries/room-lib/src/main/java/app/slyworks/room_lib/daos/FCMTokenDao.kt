package app.slyworks.room_lib.daos

import androidx.room.*
import app.slyworks.room_lib.room_models.FCMToken
import kotlinx.coroutines.flow.Flow


/**
 *Created by Joshua Sylvanus, 10:00 AM, 13/05/2022.
 */
@Dao
interface FCMTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(token: FCMToken)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateToken(token: FCMToken):Int

    @Query("SELECT * FROM FCMToken")
    suspend fun observeToken(): Flow<MutableList<FCMToken>>

    /*should return a list with one element if i did my job properly*/
    @Query("SELECT * FROM FCMToken")
    suspend fun getToken():MutableList<FCMToken>
}