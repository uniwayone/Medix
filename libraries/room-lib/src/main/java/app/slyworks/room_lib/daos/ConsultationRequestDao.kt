package app.slyworks.room_lib.daos

import androidx.room.*
import app.slyworks.room_lib.room_models.ConsultationRequest
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

@Dao
interface ConsultationRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addConsultationRequest(vararg consultationRequest: ConsultationRequest)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateConsultationRequest(vararg consultationRequest: ConsultationRequest)

    @Delete
    fun deleteConsultationRequest(vararg consultationRequest: ConsultationRequest)

    @Query("SELECT * FROM ConsultationRequest ORDER BY timestamp ASC")
    fun observeConsultationRequests():Flowable<MutableList<ConsultationRequest>>

    @Query("SELECT * FROM ConsultationRequest ORDER BY timestamp ASC")
    fun getConsultationRequestsAsync(): Observable<MutableList<ConsultationRequest>>

    @Query("SELECT * FROM ConsultationRequest ORDER BY timestamp ASC")
    fun getConsultationRequests():MutableList<ConsultationRequest>
}
