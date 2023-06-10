package app.slyworks.room_lib.daos

import androidx.room.*
import app.slyworks.room_lib.room_models.CallHistory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single


/**
 *Created by Joshua Sylvanus, 6:04 PM, 12/05/2022.
 */
@Dao
interface CallHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCallHistory(vararg callHistory: CallHistory)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCallHistory(vararg callHistory: CallHistory)

    @Delete
    fun deleteCallHistory(vararg callHistories: CallHistory)

    @Query("SELECT * FROM CallHistory")
    fun observeCallHistory(): Flowable<MutableList<CallHistory>>

    @Query("SELECT * FROM CallHistory")
     fun getCallHistory(): MutableList<CallHistory>

     @Query("SELECT COUNT(*) FROM CallHistory")
     fun getCallHistoryCount(): Long
}