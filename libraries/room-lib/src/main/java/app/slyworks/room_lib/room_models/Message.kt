package app.slyworks.room_lib.room_models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import app.slyworks.constants_lib.INCOMING_MESSAGE
import app.slyworks.constants_lib.NOT_SENT
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 6:16 PM, 1/9/2022.
 */
@Entity(indices = [Index(value = ["from_uid","to_uid"])] )
data class Message(
    @ColumnInfo(name = "type") val type:String,
    @ColumnInfo(name = "from_uid") val fromUID:String,
    @ColumnInfo(name = "to_uid") val toUID:String,
    @ColumnInfo(name = "sender_fullname") var senderFullName:String,
    @ColumnInfo(name = "receiver_fullname") var receiverFullName:String,
    @ColumnInfo(name = "content") val content:String,
    @PrimaryKey
    @ColumnInfo(name = "time_stamp") val timeStamp:String,
    @ColumnInfo(name = "message_id") val messageID:String,
    @ColumnInfo(name = "status") var status:Double,
    @ColumnInfo(name = "sender_image_uri") var senderImageUri:String,
    @ColumnInfo(name = "account_type") val accountType:String,
    @ColumnInfo(name = "sender_fcm_registration_token") var FCMRegistrationToken:String,
    @ColumnInfo(name = "receiver_image_uri") var receiverImageUri:String)
    : Comparable<Message> {
    override fun compareTo(other: Message): Int {
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