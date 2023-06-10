package app.slyworks.base_feature.custom_views

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.slyworks.base_feature.R
import app.slyworks.constants_lib.COORDINATOR
import app.slyworks.constants_lib.GENERAL
import app.slyworks.constants_lib.MAIN
import app.slyworks.utils_lib.utils.displayImage
import app.slyworks.utils_lib.utils.px
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*


/**
 *Created by Joshua Sylvanus, 8:38 AM, 26/04/2022.
 */

fun animateIn(view:View){
    with(ObjectAnimator.ofFloat(view, "translationY", -0.25f, 1f)){
        duration = 1_000
        start()
        addListener(onEnd = {} )
    }
}

fun animateOut(view:View){
    with(ObjectAnimator.ofFloat(view, "translationY", 1f, -0.25f)){
        duration = 1_000
        start()
    }
}

fun NetworkStatusView.setStatus(status:Boolean){
    if(!status){
        this@setStatus.findViewById<CircleImageView>(R.id.ivNetworkNotifier)
            .setImageResource(R.color.appRed)
        this@setStatus.findViewById<TextView>(R.id.tvStatus_network_status)
            .setText("offline")

        val anim = AnimationUtils.loadAnimation(this@setStatus.context, R.anim.network_notifier_offline)
        anim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                isVisible = true
            }
        })
        startAnimation(anim)
    }else{
        if(visibility != View.VISIBLE)
            return

        CoroutineScope(Dispatchers.Main).launch {
             this@setStatus.findViewById<CircleImageView>(R.id.ivNetworkNotifier)
                 .setImageResource(R.color.appGreen)
             this@setStatus.findViewById<TextView>(R.id.tvStatus_network_status)
                 .setText("online")

            delay(1_000)

            val anim = AnimationUtils.loadAnimation(this@setStatus.context, R.anim.network_notifier_online)
            anim.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    isVisible = false
                }
            })
            startAnimation(anim)
        }

    }
}

@IntDef(GENERAL, COORDINATOR, MAIN)
@Retention(AnnotationRetention.SOURCE)
annotation class LayoutType()

class NetworkStatusView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr:Int = 0): ConstraintLayout(context,attrs,defStyleAttr) {

    //region Vars
    private var view:View
    private var ivStatus:CircleImageView
    private var tvStatus: TextView

    private val mColorOfflineIV_id:Int = R.color.appRed
    private val mColorOnlineIV_id:Int = R.color.appGreen
    private val mColorOffline:Int = ContextCompat.getColor(context, R.color.appBackground)
    private val mColorOfflineIV:Int = ContextCompat.getColor(context, R.color.appRed)
    private val mColorOnlineIV:Int = ContextCompat.getColor(context, R.color.appGreen)
    private val mColorOnline:Int = ContextCompat.getColor(context, R.color.appCardBlue)

    private var mHasBeenInitialized:Boolean = false
    //endregion

    init{
        view = inflate(context, R.layout.network_notifier, this)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                   ViewGroup.LayoutParams.MATCH_PARENT)

        ivStatus = findViewById(R.id.ivNetworkNotifier)
        tvStatus = findViewById(R.id.tvStatus_network_status)

        //animateViewsIn()
    }

    private fun animateViewsIn(){
        viewTreeObserver.addOnPreDrawListener(
            object: ViewTreeObserver.OnPreDrawListener{
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)

                    val animation: AnimatorSet = AnimatorInflater.loadAnimator(
                        context,R.animator.network_status_view_offline_animator) as AnimatorSet
                    animation.setTarget(view)
                    animation.start()

                    mHasBeenInitialized = true
                    return false
                }
            })
    }

    companion object{
        //region formerly-used recursion code
        private fun checkForExistingView(v:ViewGroup): NetworkStatusView?{
            for(i in 0 until v.childCount){
                val child:View = v.getChildAt(i)
                val returned = _checkForExistingView(child)

                if(returned is NetworkStatusView)
                    return returned
            }

            return null
        }
        private fun _checkForExistingView(v:View): NetworkStatusView? {
            when (v) {
                is NetworkStatusView -> {
                    return v
                }
                is ViewGroup -> {
                    for (i in 0 until v.childCount) {
                        val child: View = v.getChildAt(i)

                        if (child is NetworkStatusView)
                            return child
                        else
                            _checkForExistingView(child)
                    }
                }

                else -> {
                    return null
                }
            }

            return null
        }
        //endregion

        @JvmStatic
        fun from(parent: ViewGroup, @LayoutType type:Int): NetworkStatusView {
          val view: NetworkStatusView = NetworkStatusView(parent.context)
          view.setId(View.generateViewId())

          when(type){
              GENERAL ->{
                  val constraintSet:ConstraintSet = ConstraintSet()
                  constraintSet.clone(parent as ConstraintLayout)

                  val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                      ConstraintLayout.LayoutParams.WRAP_CONTENT)
                  view.layoutParams = layoutParams

                  parent.addView(view)

                  constraintSet.connect(view.id, ConstraintSet.START, parent.id, ConstraintSet.START)
                  constraintSet.connect(view.id, ConstraintSet.END, parent.id, ConstraintSet.END)
                  constraintSet.connect(view.id, ConstraintSet.BOTTOM, parent.id, ConstraintSet.BOTTOM)


                  constraintSet.constrainWidth(view.id, ConstraintSet.MATCH_CONSTRAINT)
                  constraintSet.constrainHeight(view.id, ConstraintSet.WRAP_CONTENT)

                  /*re-aligning the main container to give the space at the bottom*/
                  val main_container:View = parent.children.first()
                  main_container.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                      ViewGroup.LayoutParams.WRAP_CONTENT)

                  constraintSet.connect(main_container.id, ConstraintSet.START, parent.id, ConstraintSet.START)
                  constraintSet.connect(main_container.id, ConstraintSet.END, parent.id, ConstraintSet.END)
                  constraintSet.connect(main_container.id, ConstraintSet.TOP, parent.id, ConstraintSet.TOP)
                  constraintSet.connect(main_container.id, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP)

                  constraintSet.constrainHeight(main_container.id, ConstraintSet.MATCH_CONSTRAINT)

                  constraintSet.applyTo(parent)
              }
              COORDINATOR ->{
                  view.layoutParams =
                      CoordinatorLayout.LayoutParams(
                          CoordinatorLayout.LayoutParams.MATCH_PARENT,
                          CoordinatorLayout.LayoutParams.WRAP_CONTENT)
                          .apply {
                      gravity = Gravity.BOTTOM
                  }

                  parent.addView(view)

                  val main_container:View = parent.children.first()
                  main_container.layoutParams = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                      CoordinatorLayout.LayoutParams.MATCH_PARENT)
                      .apply{
                          anchorId = view.id
                          anchorGravity = Gravity.TOP
                          bottomMargin = 25.px
                      }
              }
              MAIN ->{
                  val bnvLayout = parent.children.toList()[1]
                  view.layoutParams =
                      CoordinatorLayout.LayoutParams(
                          CoordinatorLayout.LayoutParams.MATCH_PARENT,
                          CoordinatorLayout.LayoutParams.WRAP_CONTENT)
                          .apply {
                              anchorId = bnvLayout.id
                              anchorGravity = Gravity.TOP
                              bottomMargin = bnvLayout.height
                          }

                  parent.addView(view)
              }
          }

          view.isVisible = false
          return view
        }
    }

    fun setVisibilityStatus(status:Boolean){
        if(status)
            makeInvisible()
        else
            makeVisible()
    }

    /*TODO:revisit this animation methods later*/
    private fun makeVisible2(){
        findViewTreeLifecycleOwner()!!.lifecycleScope.launch{
            this@NetworkStatusView.visibility = View.VISIBLE
            view.setBackgroundColor(mColorOffline)
            ivStatus.setImageResource(mColorOfflineIV_id)
            tvStatus.text = "offline"

            if(!mHasBeenInitialized)
                return@launch

            delay(2000)

            val animation: AnimatorSet = AnimatorInflater.loadAnimator(
                context, R.animator.network_status_view_offline_animator
            ) as AnimatorSet
            animation.setTarget(this)
            animation.start()
        }

    }

    private fun makeInvisible2(){
        findViewTreeLifecycleOwner()!!.lifecycleScope.launch{
            view.setBackgroundColor(mColorOnline)
            ivStatus.setImageResource(mColorOnlineIV_id)
            tvStatus.text = "online"

            delay(2000)

            val anim = AnimationUtils.loadAnimation(context,R.anim.network_notifier_offline)
            this@NetworkStatusView.startAnimation(anim)
            this@NetworkStatusView.visibility = View.GONE
        }

    }

    private fun makeVisible() {
        this@NetworkStatusView.visibility = View.VISIBLE
    }
    private fun makeInvisible() {
        this@NetworkStatusView.visibility = View.INVISIBLE
    }

}