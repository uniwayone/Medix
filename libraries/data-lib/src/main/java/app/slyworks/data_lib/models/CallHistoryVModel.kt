package app.slyworks.data_lib.models

import android.os.Parcelable
import app.slyworks.constants_lib.NOT_SET
import app.slyworks.room_lib.room_models.CallHistory
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 10:12 PM, 25/11/2022.
 */
@Parcelize
data class CallHistoryVModel(
    var type:Int = NOT_SET,
    var status:Int = NOT_SET,
    var callerUID:String = "",
    var callerName:String = "",
    var senderImageUri:String = "",
    var timeStamp:String = "",
    var duration:String = ""): Parcelable, Comparable<CallHistory>{

    constructor():this(
        type = NOT_SET,
        status = NOT_SET,
        callerUID = "",
        callerName = "",
        senderImageUri = "",
        timeStamp = "",
        duration = "")

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