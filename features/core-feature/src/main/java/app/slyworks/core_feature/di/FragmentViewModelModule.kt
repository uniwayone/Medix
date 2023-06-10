package app.slyworks.core_feature.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.slyworks.constants_lib.DI_FRAGMENT_VIEWMODEL_KEY
import app.slyworks.core_feature.MViewModelFactory
import app.slyworks.core_feature.calls_history.CallsHistoryFragmentViewModel
import app.slyworks.core_feature.chat.ChatFragmentViewModel
import app.slyworks.core_feature.chat.ChatHostFragmentViewModel
import app.slyworks.core_feature.home.HomeFragmentViewModel
import app.slyworks.core_feature.view_profile.ViewProfileFragmentViewModel
import app.slyworks.di_base_lib.FragmentScope
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider


/**
 * Created by Joshua Sylvanus, 6:13 PM, 05-Dec-2022.
 */

@Module
object FragmentViewModelModule {
    @Provides
    @Named(DI_FRAGMENT_VIEWMODEL_KEY)
    @FragmentScope
    fun provideViewModelFactory(map: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
            : MViewModelFactory =
        MViewModelFactory(map)

    @Provides
    @IntoMap
    @ViewModelKey(CallsHistoryFragmentViewModel::class)
    @FragmentScope
    fun provideCallsHistoryViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity)
    : CallsHistoryFragmentViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(CallsHistoryFragmentViewModel::class.java)

    @Provides
    @IntoMap
    @ViewModelKey(ChatHostFragmentViewModel::class)
    @FragmentScope
    fun provideChatHostFragmentViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity)
    : ChatHostFragmentViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(ChatHostFragmentViewModel::class.java)

    @Provides
    @IntoMap
    @ViewModelKey(ChatFragmentViewModel::class)
    @FragmentScope
    fun provideChatFragmentViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity)
    : ChatFragmentViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(ChatFragmentViewModel::class.java)

    @Provides
    @IntoMap
    @ViewModelKey(HomeFragmentViewModel::class)
    @FragmentScope
    fun provideHomeFragmentViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity)
    : HomeFragmentViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(HomeFragmentViewModel::class.java)

    @Provides
    @IntoMap
    @ViewModelKey(ViewProfileFragmentViewModel::class)
    @FragmentScope
    fun provideViewProfileFragmentViewModel(viewModelFactory: MViewModelFactory,
                                       activity: AppCompatActivity)
    : ViewProfileFragmentViewModel
            = ViewModelProvider(activity.viewModelStore,viewModelFactory)
        .get(ViewProfileFragmentViewModel::class.java)


}