package app.slyworks.controller_lib

data class Subscription(val event:String, val observer: Observer, var notifyMethod: NotifyMethod)