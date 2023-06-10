package app.slyworks.medix.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.auth_feature.di.MViewModelFactory
import app.slyworks.constants_lib.DI_ACTIVITY_VIEWMODEL_KEY
import app.slyworks.medix.splash.SplashActivityViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider


/**
 * Created by Joshua Sylvanus, 10:03 PM, 02-Dec-2022.
 */
@Module
object ApplicationModule{
    @Provides
    @Named(DI_ACTIVITY_VIEWMODEL_KEY)
    fun provideViewModelFactory(map: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
    : MViewModelFactory =
        MViewModelFactory(map)

    @Provides
    @IntoMap
    @ViewModelKey(SplashActivityViewModel::class)
    fun provideSplashActivityViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity)
    : SplashActivityViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(SplashActivityViewModel::class.java)

}