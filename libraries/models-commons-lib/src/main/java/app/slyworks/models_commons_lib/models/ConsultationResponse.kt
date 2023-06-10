package app.slyworks.models_commons_lib.models

import android.os.Parcelable
import app.slyworks.constants_lib.REQUEST_PENDING
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConsultationResponse(
    var toUID:String = "",
    var fromUID: String = "",
    var toFCMRegistrationToken:String = "",
    var status:String = REQUEST_PENDING,
    var fullName:String = ""
): Parcelable {
        constructor():this(toUID = "", fromUID = "", toFCMRegistrationToken = "", status = REQUEST_PENDING, fullName = "")
    }