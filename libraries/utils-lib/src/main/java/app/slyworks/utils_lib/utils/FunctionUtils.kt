package app.slyworks.utils_lib.utils


/**
 * Created by Joshua Sylvanus, 10:50 AM, 24/11/2022.
 */

inline fun <E> List<E>.isLastItem():Boolean = this.size == 1

inline fun<E> MutableList<E>.addMultiple(vararg elements:E):Unit = forEach { add(it) }