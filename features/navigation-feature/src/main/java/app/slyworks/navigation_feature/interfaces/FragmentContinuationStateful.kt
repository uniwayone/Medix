package app.slyworks.navigation_feature.interfaces

import app.slyworks.navigation_feature.interfaces.FragmentContinuation


/**
 *Created by Joshua Sylvanus, 6:00 AM, 25/08/2022.
 */
interface FragmentContinuationStateful : FragmentContinuation {
    fun popBackStack(also: ((String) -> Unit)? = null):Boolean
    fun onDestroy(block: (() -> Unit)? = null)
}