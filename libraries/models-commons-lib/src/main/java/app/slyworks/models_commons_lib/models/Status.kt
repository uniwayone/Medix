package app.slyworks.models_commons_lib.models


/**
 *Created by Joshua Sylvanus, 6:48 AM, 12/11/2021.
 */

sealed class Status<T> {
    class Loading<T> : Status<T>()
    data class Loaded<T>(val data: T) : Status<T>()
    data class Error<T>(val message: String) : Status<T>()

    companion object {
        fun <T> loading(): Status<T> { return Loading<T>() }
        fun <T> loaded(data: T): Status<T> { return Loaded(data) }
        fun <T> error(message: String): Status<T> { return Error<T>(message) }
    }
}