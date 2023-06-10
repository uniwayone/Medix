package app.slyworks.data_lib.models

import android.os.Parcelable
import app.slyworks.constants_lib.NOT_SENT
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 8:23 PM, 25/11/2022.
 */
@Parcelize
data class MessageVModel(
    var type:String = "",
    var fromUID:String = "",
    var toUID:String = "",
    var senderFullName:String = "",
    var receiverFullName:String = "",
    var content:String = "",
    var timeStamp:String = "",
    var messageID:String = "",
    var status:Double = NOT_SENT,
    var senderImageUri:String = "",
    var accountType:String = "",
    var FCMRegistrationToken:String = "",
    var receiverImageUri:String = ""

): Parcelable, Comparable<MessageVModel> {

    constructor():this(
        type = "",
        fromUID = "",
        toUID = "",
        senderFullName = "",
        receiverFullName = "",
        content = "",
        timeStamp = "",
        messageID = "",
        status = NOT_SENT,
        senderImageUri = "",
        accountType = "",
        FCMRegistrationToken = "",
        receiverImageUri = "")

    override fun compareTo(other: MessageVModel): Int {
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
