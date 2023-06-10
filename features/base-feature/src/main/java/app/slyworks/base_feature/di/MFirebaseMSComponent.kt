package app.slyworks.base_feature.di

import android.content.Context
import app.slyworks.auth_lib.di.AuthApplicationScopedComponent
import app.slyworks.base_feature.services.MFirebaseMessagingService
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.AppComponent
import app.slyworks.di_base_lib.MFirebaseMSScope
import app.slyworks.network_lib.di.NetworkComponent
import app.slyworks.utils_lib.di.UtilsComponent
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 10:38 AM, 15-Dec-2022.
 */
@Component(
    modules = [MFirebaseMSModule::class],
   /* dependencies = [
        Context::class,
        UtilsComponent::class,
        AuthApplicationScopedComponent::class,
        DataComponent::class,
        NetworkComponent::class
    ]*/
)
@MFirebaseMSScope
interface MFirebaseMSComponent {
    companion object{
        @JvmStatic
        fun getInitialBuilder():MFirebaseMSComponent.Builder =
            DaggerMFirebaseMSComponent.builder()
                .setContext(AppComponent.getContext())
                .setUtilsComponent(UtilsComponent.getInstance())
                .setAuthComponent(AuthApplicationScopedComponent.getInstance())
                .setDataComponent(DataComponent.getInstance())
                .setNetworkComponent(NetworkComponent.getInstance())
    }

    fun inject(service:MFirebaseMessagingService)

    @Component.Builder
    public interface Builder{
        fun setContext(@BindsInstance context: Context):Builder
        fun setUtilsComponent(@BindsInstance comp: UtilsComponent):Builder
        fun setAuthComponent(@BindsInstance comp: AuthApplicationScopedComponent):Builder
        fun setDataComponent(@BindsInstance comp: DataComponent):Builder
        fun setNetworkComponent(@BindsInstance comp: NetworkComponent):Builder
        fun build():MFirebaseMSComponent
    }
}