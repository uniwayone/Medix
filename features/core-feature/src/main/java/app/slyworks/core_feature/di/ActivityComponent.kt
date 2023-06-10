package app.slyworks.core_feature.di

import androidx.appcompat.app.AppCompatActivity
import app.slyworks.auth_lib.di.AuthApplicationScopedComponent
import app.slyworks.base_feature.di.BaseFeatureComponent
import app.slyworks.communication_lib.di.CommunicationComponent
import app.slyworks.core_feature.main.MainActivity
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.FeatureScope
import app.slyworks.network_lib.di.NetworkComponent
import app.slyworks.utils_lib.di.UtilsComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent


/**
 * Created by Joshua Sylvanus, 6:28 PM, 05-Dec-2022.
 */

@Component(
    modules = [ActivityViewModelModule::class],
    dependencies = [
        BaseFeatureComponent::class,
        DataComponent::class,
        AuthApplicationScopedComponent::class,
        CommunicationComponent::class,
        NetworkComponent::class,
        UtilsComponent::class,
        AppCompatActivity::class
    ]
)
@FeatureScope
interface ActivityComponent {
    companion object{
        @JvmStatic
        fun getInitialBuilder():DaggerActivityComponent.Builder =
            DaggerActivityComponent.builder()
                .baseFeatureComponent(
                    BaseFeatureComponent.getInstance())
                .dataComponent(
                    DataComponent.getInstance())
                .authApplicationScopedComponent(
                    AuthApplicationScopedComponent.getInstance())
                .communicationComponent(
                    CommunicationComponent.getInstance())
                .networkComponent(
                    NetworkComponent.getInstance())
                .utilsComponent(
                    UtilsComponent.getInstance())
    }

    fun inject(activity:MainActivity)

    fun fragmentComponentBuilder(): FragmentComponent.Builder
}