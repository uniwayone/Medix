package app.slyworks.di_base_lib

import javax.inject.Scope


/**
 * Created by Joshua Sylvanus, 8:25 PM, 15/10/2022.
 */

/** @Scope for object bound to the Base Activity lifecycle
 * similar to Application Scope but is initialised from when MainActivity starts */
@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseActivityScope()
