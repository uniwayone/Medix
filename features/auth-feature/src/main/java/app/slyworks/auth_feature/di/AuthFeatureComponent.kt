package app.slyworks.auth_feature.di

import androidx.appcompat.app.AppCompatActivity
import app.slyworks.auth_feature.login.LoginActivity
import app.slyworks.auth_feature.onboarding.OnBoardingActivity
import app.slyworks.auth_feature.registration.RegistrationActivity
import app.slyworks.auth_feature.verification.VerificationActivity
import app.slyworks.auth_lib.di.AuthActivityScopedComponent
import app.slyworks.auth_lib.di.AuthApplicationScopedComponent
import app.slyworks.base_feature.di.BaseFeatureComponent
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.FeatureScope
import app.slyworks.network_lib.di.NetworkComponent
import app.slyworks.utils_lib.di.UtilsComponent
import dagger.Component


/**
 * Created by Joshua Sylvanus, 6:53 AM, 25/11/2022.
 */
@Component(
    modules = [AuthFeatureModule::class],
    dependencies = [
        AuthApplicationScopedComponent::class,
        AuthActivityScopedComponent::class,
        DataComponent::class,
        NetworkComponent::class,
        BaseFeatureComponent::class,
        UtilsComponent::class,
        AppCompatActivity::class
    ])
@FeatureScope
interface AuthFeatureComponent {
    companion object{
        @JvmStatic
        fun getInitialBuilder():DaggerAuthFeatureComponent.Builder =
            DaggerAuthFeatureComponent.builder()
                .authApplicationScopedComponent(
                    AuthApplicationScopedComponent.getInstance())
                .authActivityScopedComponent(
                    AuthActivityScopedComponent.getInstance())
                .dataComponent(
                    DataComponent.getInstance())
                .networkComponent(
                    NetworkComponent.getInstance())
                .baseFeatureComponent(
                    BaseFeatureComponent.getInstance())
                .utilsComponent(
                    UtilsComponent.getInstance())
    }

    fun inject(activity:OnBoardingActivity)
    fun inject(activity:LoginActivity)
    fun inject(activity:VerificationActivity)
    fun inject(activity:RegistrationActivity)
}