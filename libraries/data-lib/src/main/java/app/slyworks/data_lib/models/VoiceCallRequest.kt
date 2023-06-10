package app.slyworks.data_lib.models

import android.os.Parcelable
import app.slyworks.constants_lib.REQUEST_PENDING
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoiceCallRequest(
    var details: FBUserDetailsVModel = FBUserDetailsVModel(),
    var status: String = REQUEST_PENDING
) : Parcelable {
    constructor() : this(
        details = FBUserDetailsVModel(),
        status = "")
}
