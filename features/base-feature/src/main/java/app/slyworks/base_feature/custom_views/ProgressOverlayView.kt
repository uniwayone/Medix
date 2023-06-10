package app.slyworks.base_feature.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.allViews
import androidx.core.view.isVisible
import app.slyworks.base_feature.R


/**
 *Created by Joshua Sylvanus, 9:17 AM, 10-Dec-2022.
 */
class ProgressOverlayView
@JvmOverloads
constructor(
    context: Context,
    attrs:AttributeSet? = null,
    defStyleAttr:Int = 0):ConstraintLayout(context,attrs,defStyleAttr) {

    //region Vars
        private var view: View
    //endregion

        init {
            view = inflate(context, R.layout.layout_progress_overlay, this)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)

            view.setOnClickListener({})
        }

    companion object{
        @JvmStatic
        fun get(activity:AppCompatActivity):ProgressOverlayView{
         val view:ProgressOverlayView = ProgressOverlayView(activity)
         view.setId(View.generateViewId())

         activity.findViewById<ViewGroup>(android.R.id.content)
             .addView(view)

         return view
       }
    }
}