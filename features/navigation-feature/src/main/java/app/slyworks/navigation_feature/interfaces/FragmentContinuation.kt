package app.slyworks.navigation_feature.interfaces

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment


/**
 *Created by Joshua Sylvanus, 5:59 AM, 25/08/2022.
 */
interface FragmentContinuation {
    fun into(@IdRes containerID: Int): FragmentContinuation
    fun hideCurrent(): FragmentContinuation
    fun show(f: Fragment, currentTag: String?): FragmentContinuation
    fun show(f: Fragment): FragmentContinuation
    fun after(block: () -> Unit): FragmentContinuation
    fun navigate()
}