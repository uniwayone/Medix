package app.slyworks.room_lib.room_models

import app.slyworks.constants_lib.NOT_SENT


/**
 *Created by Joshua Sylvanus, 4:33 PM, 1/11/2022.
 */
data class MessageDetails(
    var userAccountType:String = "",
    val lastMessageType:String? = "",
    var lastMessageContent:String? = "",
    var lastMessageStatus:Double = NOT_SENT,
    var lastMessageTimeStamp:String? = "",
    var senderImageUri:String = "",
    var fullName:String = ""){
    constructor():this(
        userAccountType = "",
        lastMessageType = "",
        lastMessageContent = "",
        lastMessageStatus = NOT_SENT,
        lastMessageTimeStamp = "",
        senderImageUri = "")
}
