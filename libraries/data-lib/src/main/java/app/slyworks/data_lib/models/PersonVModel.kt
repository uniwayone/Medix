package app.slyworks.data_lib.models

import android.os.Parcelable
import app.slyworks.constants_lib.NOT_SENT
import app.slyworks.room_lib.room_models.Person
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 10:12 PM, 25/11/2022.
 */

@Parcelize
data class PersonVModel(
    var firebaseUID:String = "",
    var userAccountType:String = "",
    var lastMessageType:String = "",
    var lastMessageContent:String? = "",
    var lastMessageStatus:Double = NOT_SENT,
    var lastMessageTimeStamp:String = "",
    var imageUri:String = "",
    var fullName:String = "",
    var unreadMessageCount:Int = 0,
    var FCMRegistrationToken:String = "")
    : Parcelable {
    constructor() : this(
        firebaseUID = "",
        userAccountType = "",
        lastMessageType = "",
        lastMessageContent = "",
        lastMessageStatus = NOT_SENT,
        lastMessageTimeStamp = "",
        imageUri = "",
        fullName = "",
        unreadMessageCount = 0,
        FCMRegistrationToken = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person
        if (firebaseUID != other.firebaseUID) return false

        return true
    }

    override fun hashCode(): Int {
        return firebaseUID.hashCode()
    }


}
