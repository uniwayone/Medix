package app.slyworks.controller_lib

/**
 *Created by Joshua Sylvanus, 11:57 AM, 12/10/2021.
 */
data class Subscriber(
    var observer: Observer,
    var notifyMethod: NotifyMethod = NotifyMethod.PUSH_IMMEDIATELY
)
