package app.slyworks.navigation_feature.interfaces

import android.view.View


/**
 *Created by Joshua Sylvanus, 5:59 AM, 25/08/2022.
 */
interface ActivityContinuation {
    fun addSharedElementTransition(view:View, transitionViewName:String): ActivityContinuation
    fun addExtra(key:String, value:Any): ActivityContinuation
    fun setPackageName(packageName:String): ActivityContinuation
    fun clearTop(): ActivityContinuation
    fun newAndClearTask(): ActivityContinuation
    fun previousIsTop(): ActivityContinuation
    fun finishCaller(): ActivityContinuation
    fun navigate()
}