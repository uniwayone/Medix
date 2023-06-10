package app.slyworks.models_commons_lib.models

import app.slyworks.constants_lib.NOT_SENT


/**
 * Created by Joshua Sylvanus, 8:38 PM, 18/11/2022.
 */
data class MessageCloudMessage(
    val fromUID:String,
    val toUID:String,
    val senderFullName:String,
    val receiverFullName:String,
    val content:String,
    val timeStamp:String,
    val messageID:String,
    val status:Double = NOT_SENT,
    val senderImageUri:String,
    val accountType:String,
    val FCMRegistrationToken:String,
    val receiverImageUri:String,
    override var type:String): Data
