package app.slyworks.data_lib.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 8:22 PM, 25/11/2022.
 */
@Parcelize
data class FBUserDetailsVModel(
     var accountType: String = "",
     var firstName: String = "",
     var lastName: String = "",
     var fullName:String = "",
     var email: String = "",
     var sex:String = "",
     var age:String = "",
     var firebaseUID: String = "",
     var agoraUID: String = "",
     var FCMRegistrationToken:String = "",
     var imageUri: String = "",
     var history: MutableList<String>? = mutableListOf(),
     var specialization: MutableList<String>? = mutableListOf() ): Parcelable {

    constructor():
            this(accountType= "",
                firstName = "",
                lastName = "",
                fullName = "",
                email = "",
                sex = "",
                age ="",
                firebaseUID = "",
                agoraUID = "",
                FCMRegistrationToken = "",
                imageUri = "",
                history = mutableListOf(),
                specialization = mutableListOf())
}

