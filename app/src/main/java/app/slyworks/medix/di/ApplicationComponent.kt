package app.slyworks.medix.di

import androidx.appcompat.app.AppCompatActivity
import app.slyworks.auth_lib.di.AuthActivityScopedComponent
import app.slyworks.auth_lib.di.AuthApplicationScopedComponent
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.FeatureScope
import app.slyworks.medix.splash.SplashActivity
import app.slyworks.utils_lib.di.UtilsComponent
import dagger.Component


/**
 * Created by Joshua Sylvanus, 6:53 AM, 25/11/2022.
 */
@Component(
    modules = [ApplicationModule::class],
    dependencies = [
        AuthApplicationScopedComponent::class,
        AuthActivityScopedComponent::class,
        DataComponent::class,
        UtilsComponent::class,
        AppCompatActivity::class
    ])
@FeatureScope
interface ApplicationComponent {
    companion object{
        @JvmStatic
        fun getInitialBuilder():DaggerApplicationComponent.Builder =
            DaggerApplicationComponent.builder()
                .authApplicationScopedComponent(
                    AuthApplicationScopedComponent.getInstance())
                .authActivityScopedComponent(
                    AuthActivityScopedComponent.getInstance())
                .dataComponent(
                    DataComponent.getInstance())
                .utilsComponent(
                    UtilsComponent.getInstance())
    }

    fun inject(activity: SplashActivity)
}