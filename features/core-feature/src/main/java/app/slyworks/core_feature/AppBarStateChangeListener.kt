package app.slyworks.core_feature

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {
    enum class AppBarState{ EXPANDED, COLLAPSED, IDLE; }

    private var currentAppBarState: AppBarState = AppBarState.IDLE

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarState)
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        when{
            verticalOffset == 0 -> {
                if(currentAppBarState != AppBarState.EXPANDED)
                    onStateChanged(appBarLayout!!, AppBarState.EXPANDED)

                currentAppBarState = AppBarState.EXPANDED
            }
            abs(verticalOffset) >= appBarLayout!!.totalScrollRange ->{
                if(currentAppBarState != AppBarState.COLLAPSED)
                    onStateChanged(appBarLayout, AppBarState.COLLAPSED)

                currentAppBarState = AppBarState.COLLAPSED
            }
            else -> {
                if(currentAppBarState != AppBarState.IDLE)
                    onStateChanged(appBarLayout, AppBarState.IDLE)

                currentAppBarState = AppBarState.IDLE
            }
        }
    }


}