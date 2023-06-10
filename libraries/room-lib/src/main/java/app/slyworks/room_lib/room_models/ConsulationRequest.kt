package app.slyworks.room_lib.room_models

import android.os.Parcelable
import androidx.room.*
import app.slyworks.constants_lib.REQUEST_PENDING
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 6:15 PM, 1/23/2022.
 */
@Entity
data class ConsultationRequest(
    @PrimaryKey
    @ColumnInfo(name = "to_uid") val toUID:String ,
    @ColumnInfo(name = "timestamp") val timeStamp:String ,
    @Embedded val details: FBUserDetails,
    @ColumnInfo(name = "status") var status: String ):
    Comparable<ConsultationRequest> {

    override fun compareTo(other: ConsultationRequest): Int {
        val thisTimeStamp:Long = this.timeStamp.toLong()
        val otherTimeStamp:Long = other.timeStamp.toLong()

        return when{
            thisTimeStamp > otherTimeStamp -> 1
            thisTimeStamp < otherTimeStamp -> -1
            thisTimeStamp == otherTimeStamp -> 0
            else -> throw UnsupportedOperationException("cannot sort order of unknown value")
        }
    }
}




