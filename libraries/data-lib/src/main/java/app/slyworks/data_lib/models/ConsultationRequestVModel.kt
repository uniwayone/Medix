package app.slyworks.data_lib.models

import android.os.Parcelable
import app.slyworks.constants_lib.REQUEST_PENDING
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 10:12 PM, 25/11/2022.
 */
@Parcelize
data class ConsultationRequestVModel(
    val toUID:String = "",
    val timeStamp:String = "",
    val details: FBUserDetailsVModel = FBUserDetailsVModel(),
    var status: String = REQUEST_PENDING )
    : Parcelable, Comparable<ConsultationRequestVModel> {

    constructor():this(toUID = "", timeStamp = "", details = FBUserDetailsVModel(), status = REQUEST_PENDING)

    override fun compareTo(other: ConsultationRequestVModel): Int {
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

