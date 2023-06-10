package app.slyworks.video_call_feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider

/**
 *Created by Joshua Sylvanus, 7:53 PM, 02/12/2022.
 */
class MViewModelFactory
constructor(private val viewModels:
     Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(VideoCallViewModel::class.java) ->
                viewModels[VideoCallViewModel::class.java] as T

            else -> throw IllegalArgumentException("please add class to MViewModelFactory before trying to instantiate it")
        }
    }
}
