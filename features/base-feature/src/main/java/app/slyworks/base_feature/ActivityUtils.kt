package app.slyworks.base_feature

import androidx.appcompat.app.AppCompatActivity


/**
 *Created by Joshua Sylvanus, 10:08 PM, 18/05/2022.
 */
object ActivityUtils {
    //region Vars
    private var foregroundStatus:Boolean = true
    private var count:Int = 0
    private var currentActivityTag:String = ""
    //endregion

    fun from(simpleName:String):Class<out AppCompatActivity>{
        return when(simpleName){
            else -> throw IllegalArgumentException("fix the \"from\" method")
        }
    }


    fun setForegroundStatus(status:Boolean, tag:String){
        if(status){
            currentActivityTag = tag
            foregroundStatus = true
        }else if(!status && (tag == currentActivityTag)){
            foregroundStatus = status
        }
    }

    fun isAppInForeground():Boolean =  foregroundStatus

    fun isLastActivity():Boolean = count == 1
    fun incrementActivityCount():Int = count++
    fun decrementActivityCount():Int = count--

}

