package app.slyworks.core_feature.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.constants_lib.DI_ACTIVITY_VIEWMODEL_KEY
import app.slyworks.core_feature.MViewModelFactory
import app.slyworks.core_feature.main.MainActivity
import app.slyworks.core_feature.main.MainActivityViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider


/**
 * Created by Joshua Sylvanus, 6:13 PM, 05-Dec-2022.
 */

@Module
object ActivityViewModelModule {
    @Provides
    @Named(DI_ACTIVITY_VIEWMODEL_KEY)
    fun provideViewModelFactory(map: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
            : MViewModelFactory =
        MViewModelFactory(map)

    @Provides
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    fun provideMainActivityViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity)
    : MainActivityViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(MainActivityViewModel::class.java)
}