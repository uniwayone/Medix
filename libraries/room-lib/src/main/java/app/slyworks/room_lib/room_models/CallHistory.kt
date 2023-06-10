package app.slyworks.room_lib.room_models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.slyworks.constants_lib.NOT_SET
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 5:58 PM, 12/05/2022.
 */

@Entity
data class CallHistory(
    @ColumnInfo(name = "type")val type:Int,
    @ColumnInfo(name = "status")val status:Int,
    @ColumnInfo(name = "caller_uid")val callerUID:String,
    @ColumnInfo(name = "name")val callerName:String,
    @ColumnInfo(name = "sender_image_uri")val senderImageUri:String,
    @PrimaryKey
    @ColumnInfo(name = "time_stamp") val timeStamp:String,
    @ColumnInfo(name = "duration") val duration:String): Comparable<CallHistory>{

    override fun compareTo(other: CallHistory): Int {
        val otherTimeStamp: Long = other.timeStamp.toLong()
        if (this.timeStamp.toLong() > otherTimeStamp)
            return 1
        else if (this.timeStamp.toLong() < otherTimeStamp)
            return -1
        else if (this.timeStamp.toLong() == otherTimeStamp)
            return 0
        else
            throw UnsupportedOperationException("cannot sort order of unknown value")
    }
}
