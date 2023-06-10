package app.slyworks.core_feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.core_feature.calls_history.CallsHistoryFragmentViewModel
import app.slyworks.core_feature.chat.ChatFragmentViewModel
import app.slyworks.core_feature.chat.ChatHostFragmentViewModel
import app.slyworks.core_feature.home.HomeFragmentViewModel
import app.slyworks.core_feature.main.MainActivityViewModel
import app.slyworks.core_feature.view_profile.ViewProfileFragmentViewModel
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
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) ->
                viewModels[MainActivityViewModel::class.java] as T

            modelClass.isAssignableFrom(CallsHistoryFragmentViewModel::class.java) ->
                viewModels[CallsHistoryFragmentViewModel::class.java] as T

            modelClass.isAssignableFrom(ChatHostFragmentViewModel::class.java) ->
                viewModels[ChatHostFragmentViewModel::class.java] as T

            modelClass.isAssignableFrom(ChatFragmentViewModel::class.java) ->
                viewModels[ChatFragmentViewModel::class.java] as T

            modelClass.isAssignableFrom(HomeFragmentViewModel::class.java) ->
                viewModels[HomeFragmentViewModel::class.java] as T

            modelClass.isAssignableFrom(ViewProfileFragmentViewModel::class.java) ->
                viewModels[ViewProfileFragmentViewModel::class.java] as T

            else -> throw IllegalArgumentException("please add class to MViewModelFactory before trying to instantiate it")
        }
    }
}
