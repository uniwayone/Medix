package app.slyworks.network_lib.di

import android.content.Context
import app.slyworks.di_base_lib.AppComponent
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.NetworkLibScope
import app.slyworks.network_lib.NetworkRegister
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 7:43 PM, 02/12/2022.
 */
@Component(modules = [NetworkModule::class])
@NetworkLibScope
interface NetworkComponent {
    companion object{
        private var instance:NetworkComponent? = null

        @JvmStatic
        fun getInstance():NetworkComponent{
            if(instance == null)
                instance =
                DaggerNetworkComponent.builder()
                    .setContext(AppComponent.getContext())
                    .build()

            return instance!!
        }
    }

    fun getNetworkRegister(): NetworkRegister

    @Component.Builder
    interface Builder{
        fun setContext(@BindsInstance context: Context):Builder
        fun build():NetworkComponent
    }
}
