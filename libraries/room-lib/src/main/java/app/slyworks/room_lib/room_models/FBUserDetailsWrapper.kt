package app.slyworks.room_lib.room_models


/**
 * Created by Joshua Sylvanus, 10:54 PM, 1/17/2022.
 */

data class MessagePersonWrapper(
    var messages:MutableList<Message> = mutableListOf()){
        constructor():this(mutableListOf())
    }