package app.slyworks.di_base_lib

import javax.inject.Scope


/**
 *Created by Joshua Sylvanus, 3:26 PM, 04-Jun-22.
 */

/** @Scope for object bound to the Activity lifecycle */
@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope
