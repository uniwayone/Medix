package app.slyworks.di_base_lib

import javax.inject.Scope


/**
 *Created by Joshua Sylvanus, 3:27 PM, 04-Jun-22.
 */

/** @scope for objects bound to the Application Lifecycle*/
@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope()
