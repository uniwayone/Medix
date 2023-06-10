package app.slyworks.data_lib.models

import android.os.Parcelable
import app.slyworks.constants_lib.REQUEST_PENDING
import kotlinx.parcelize.Parcelize


/**
 *Created by Joshua Sylvanus, 12:22 PM, 1/23/2022.
 */
@Parcelize
data class VideoCallRequest(
    var details: FBUserDetailsVModel = FBUserDetailsVModel(),
    var status: String = REQUEST_PENDING): Parcelable {
    constructor():this(
        details = FBUserDetailsVModel(),
        status = "")
}
