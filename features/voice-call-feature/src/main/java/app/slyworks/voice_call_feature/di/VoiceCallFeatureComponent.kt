package app.slyworks.voice_call_feature.di

import androidx.appcompat.app.AppCompatActivity
import app.slyworks.base_feature.di.BaseFeatureComponent
import app.slyworks.communication_lib.di.CommunicationComponent
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.FeatureScope
import app.slyworks.utils_lib.di.UtilsComponent
import app.slyworks.voice_call_feature.VoiceCallActivity
import dagger.Component


/**
 * Created by Joshua Sylvanus, 7:50 PM, 05-Dec-2022.
 */

@Component(
    modules = [VoiceCallFeatureModule::class],
    dependencies = [
       BaseFeatureComponent::class,
       DataComponent::class,
       CommunicationComponent::class,
       UtilsComponent::class,
       AppCompatActivity::class
    ]
)
@FeatureScope
interface VoiceCallFeatureComponent {
    companion object{
        @JvmStatic
        fun getInitialBuilder():DaggerVoiceCallFeatureComponent.Builder =
            DaggerVoiceCallFeatureComponent.builder()
                .baseFeatureComponent(
                    BaseFeatureComponent.getInstance())
                .dataComponent(
                    DataComponent.getInstance())
                .communicationComponent(
                    CommunicationComponent.getInstance())
                .utilsComponent(
                    UtilsComponent.getInstance())
    }

   fun inject(activity: VoiceCallActivity)
}