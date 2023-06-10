package app.slyworks.auth_feature.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.auth_feature.login.LoginActivityViewModel
import app.slyworks.auth_feature.onboarding.OnBoardingActivityViewModel
import app.slyworks.auth_feature.registration.RegistrationActivityViewModel
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
            modelClass.isAssignableFrom(OnBoardingActivityViewModel::class.java) ->
                viewModels[OnBoardingActivityViewModel::class.java] as T

            modelClass.isAssignableFrom(LoginActivityViewModel::class.java) ->
                viewModels[LoginActivityViewModel::class.java] as T

            modelClass.isAssignableFrom(RegistrationActivityViewModel::class.java) ->
                viewModels[RegistrationActivityViewModel::class.java] as T

            else -> throw IllegalArgumentException("please add class to MViewModelFactory before trying to instantiate it")
        }
    }
}
