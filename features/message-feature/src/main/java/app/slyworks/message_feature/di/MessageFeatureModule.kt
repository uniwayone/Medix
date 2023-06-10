package app.slyworks.message_feature.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.constants_lib.DI_ACTIVITY_VIEWMODEL_KEY
import app.slyworks.message_feature.MViewModelFactory
import app.slyworks.message_feature.message.MessageActivityViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider


/**
 * Created by Joshua Sylvanus, 6:41 PM, 05-Dec-2022.
 */

@Module
object MessageFeatureModule {
    @Provides
    @Named(DI_ACTIVITY_VIEWMODEL_KEY)
    fun provideViewModelFactory(map: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
            : MViewModelFactory =
        MViewModelFactory(map)

    @Provides
    @IntoMap
    @ViewModelKey(MessageActivityViewModel::class)
    fun provideMessageActivityViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity
    ): MessageActivityViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(MessageActivityViewModel::class.java)

}