package app.slyworks.message_feature.di

import app.slyworks.auth_lib.di.AuthApplicationScopedComponent
import app.slyworks.base_feature.di.BaseFeatureComponent
import app.slyworks.communication_lib.di.CommunicationComponent
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.FeatureScope
import app.slyworks.message_feature.message.MessageActivity
import app.slyworks.utils_lib.di.UtilsComponent
import dagger.Component


/**
 * Created by Joshua Sylvanus, 6:43 PM, 05-Dec-2022.
 */

@Component(
    modules = [MessageFeatureModule::class],
    dependencies = [
         BaseFeatureComponent::class,
         AuthApplicationScopedComponent::class,
         CommunicationComponent::class,
         DataComponent::class,
         UtilsComponent::class,
    ]
)
@FeatureScope
interface MessageFeatureComponent {
    companion object{
        @JvmStatic
        fun getInitialBuilder():DaggerMessageFeatureComponent.Builder =
            DaggerMessageFeatureComponent.builder()
                .baseFeatureComponent(
                    BaseFeatureComponent.getInstance())
                .authApplicationScopedComponent(
                    AuthApplicationScopedComponent.getInstance())
                .communicationComponent(
                    CommunicationComponent.getInstance())
                .dataComponent(
                    DataComponent.getInstance())
                .utilsComponent(
                    UtilsComponent.getInstance())
    }

    fun inject(activity: MessageActivity)
}