package app.slyworks.controller_lib


/**
 *Created by Joshua Sylvanus, 11:57 AM, 12/10/2021.
 */
interface Observer {
    fun <T>notify(event:String, data:T? = null){
        /*default implementation*/
    }
}