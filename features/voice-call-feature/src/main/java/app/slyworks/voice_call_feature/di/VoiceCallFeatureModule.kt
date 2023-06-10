package app.slyworks.voice_call_feature.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.constants_lib.DI_ACTIVITY_VIEWMODEL_KEY
import app.slyworks.voice_call_feature.MViewModelFactory
import app.slyworks.voice_call_feature.VoiceCallViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider


/**
 * Created by Joshua Sylvanus, 7:48 PM, 05-Dec-2022.
 */
@Module
object VoiceCallFeatureModule {
    @Provides
    @Named(DI_ACTIVITY_VIEWMODEL_KEY)
    fun provideViewModelFactory(map: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
            : MViewModelFactory =
        MViewModelFactory(map)

    @Provides
    @IntoMap
    @ViewModelKey(VoiceCallViewModel::class)
    fun provideVoiceCallViewModel(viewModelFactory: MViewModelFactory,
                                        activity: AppCompatActivity)
    : VoiceCallViewModel =
        ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(VoiceCallViewModel::class.java)

}