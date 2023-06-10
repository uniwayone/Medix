package app.slyworks.base_feature.di

import android.content.Context
import app.slyworks.base_feature.ListenerManager
import app.slyworks.base_feature.VibrationManager
import app.slyworks.communication_lib.*
import app.slyworks.communication_lib.di.CommunicationComponent
import app.slyworks.di_base_lib.AppComponent
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.BaseFeatureScope
import app.slyworks.di_base_lib.CommunicationLibScope
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 9:51 PM, 02-Dec-2022.
 */

@Component(modules = [BaseFeatureModule::class])
@BaseFeatureScope
interface BaseFeatureComponent {
    companion object{
        private var instance: BaseFeatureComponent? = null

        @JvmStatic
        fun getInstance():BaseFeatureComponent{
            if(instance == null)
                instance =
                DaggerBaseFeatureComponent.builder()
                    .setContext(AppComponent.getContext())
                    .setCommunicationComponent(
                        CommunicationComponent.getInstance())
                    .build()

            return instance!!
        }
    }

    fun getListenerManager(): ListenerManager
    fun getVibrationManager(): VibrationManager

    @Component.Builder
    interface Builder{
        fun setContext(@BindsInstance context: Context):Builder
        fun setCommunicationComponent(@BindsInstance cComponent: CommunicationComponent): Builder
        fun build():BaseFeatureComponent
    }
}