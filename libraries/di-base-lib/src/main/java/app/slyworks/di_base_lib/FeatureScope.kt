package app.slyworks.di_base_lib

import javax.inject.Scope


/**
 *Created by Joshua Sylvanus, 10:19 PM, 02-Dec-2022.
 */

/** @scope for objects bound to the lifecycle of each Feature component
 * in a Feature module */
@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureScope()
