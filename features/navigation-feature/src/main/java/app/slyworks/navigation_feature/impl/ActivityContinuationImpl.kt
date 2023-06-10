package app.slyworks.navigation_feature.impl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import app.slyworks.navigation_feature.interfaces.ActivityContinuation


/**
 * Created by Joshua Sylvanus, 6:02 AM, 25/08/2022.
 */

data class ActivityContinuationImpl(@PublishedApi
                                    internal val intent: Intent,
                                    private var activity: Activity?) : ActivityContinuation {
    private var shouldFinishCaller:Boolean = false
    private var transitionPairs:MutableList<Pair<View, String>> = mutableListOf()


    override fun addSharedElementTransition(view: View, transitionViewName: String): ActivityContinuation {
        this.transitionPairs.add(Pair<View,String>(view,transitionViewName))
        return this
    }

    override fun addExtra(key: String, value: Any): ActivityContinuation {
        when(value){
            is Int -> this.intent.putExtra(key, value)
            is Double -> this.intent.putExtra(key, value)
            is Long -> this.intent.putExtra(key, value)
            is Char -> this.intent.putExtra(key, value)
            is Byte -> this.intent.putExtra(key, value)
            is Boolean -> this.intent.putExtra(key, value)
            is String -> this.intent.putExtra(key, value)
            is Parcelable -> this.intent.putExtra(key, value)
            is Bundle -> this.intent.putExtra(key, value)
            else -> throw IllegalArgumentException("the type for `value` is not supported")
        }

        return this
    }

    override fun setPackageName(packageName:String): ActivityContinuation {
        this.intent.setPackage(packageName)
        return this
    }

    override fun clearTop(): ActivityContinuation {
        this.intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return this
    }

    override fun newAndClearTask(): ActivityContinuation {
        this.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        return this
    }

    override fun previousIsTop(): ActivityContinuation {
        this.intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
        return this
    }

    fun singleTop(): ActivityContinuation {
        this.intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return this
    }

    override fun finishCaller(): ActivityContinuation {
        shouldFinishCaller = true
        return this
    }

    override fun navigate(){
        var options:ActivityOptionsCompat? = null
        if(transitionPairs.isNotEmpty())
           options = ActivityOptionsCompat.makeSceneTransitionAnimation(this.activity!!, *transitionPairs.toTypedArray())

        activity!!.startActivity(this.intent, options?.toBundle())

        if(shouldFinishCaller)
            activity!!.finish()
        activity = null
    }
}