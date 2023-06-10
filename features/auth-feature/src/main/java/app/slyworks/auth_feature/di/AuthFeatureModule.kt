package app.slyworks.auth_feature.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.auth_feature.login.LoginActivityViewModel
import app.slyworks.auth_feature.onboarding.OnBoardingActivityViewModel
import app.slyworks.auth_feature.registration.RegistrationActivityViewModel
import app.slyworks.constants_lib.DI_ACTIVITY_VIEWMODEL_KEY
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider


/**
 * Created by Joshua Sylvanus, 10:03 PM, 02-Dec-2022.
 */
@Module
object AuthFeatureModule{
    @Provides
    @Named(DI_ACTIVITY_VIEWMODEL_KEY)
    fun provideViewModelFactory(map: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
    : MViewModelFactory =
        MViewModelFactory(map)

    @Provides
    @IntoMap
    @ViewModelKey(OnBoardingActivityViewModel::class)
    fun provideOnBoardingActivityViewModel(viewModelFactory: MViewModelFactory,
                                           activity: AppCompatActivity)
    : OnBoardingActivityViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(OnBoardingActivityViewModel::class.java)

    @Provides
    @IntoMap
    @ViewModelKey(LoginActivityViewModel::class)
    fun provideLoginActivityViewModel(viewModelFactory: MViewModelFactory,
                                      activity: AppCompatActivity)
            : LoginActivityViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(LoginActivityViewModel::class.java)


    @Provides
    @IntoMap
    @ViewModelKey(RegistrationActivityViewModel::class)
    fun provideRegistrationActivityViewModel(viewModelFactory: MViewModelFactory,
                                             activity: AppCompatActivity)
    : RegistrationActivityViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(RegistrationActivityViewModel::class.java)
}