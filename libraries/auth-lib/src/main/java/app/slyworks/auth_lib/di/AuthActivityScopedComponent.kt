package app.slyworks.auth_lib.di

import app.slyworks.auth_lib.RegistrationManager
import app.slyworks.auth_lib.VerificationHelper
import app.slyworks.crypto_lib.di.CryptoComponent
import app.slyworks.di_base_lib.ActivityScope
import app.slyworks.di_base_lib.AuthLibActivityScope
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsComponent
import app.slyworks.utils_lib.di.UtilsComponent
import com.google.firebase.installations.Utils
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent


/**
 * Created by Joshua Sylvanus, 8:15 PM,  02-Dec-2022.
 */
@Subcomponent(modules = [AuthActivityScopedModule::class])
@AuthLibActivityScope
interface AuthActivityScopedComponent {
    companion object{
        private var instance:AuthActivityScopedComponent? = null

        @JvmStatic
        fun getInstance():AuthActivityScopedComponent{
            /*if(instance == null)
                instance =*/
              return AuthApplicationScopedComponent.getInstance()
                    .authActivityComponent()

               /* DaggerAuthActivityScopedComponent.builder()
                    .setCryptoComponent(
                        CryptoComponent.getInstance())
                    .setFirebaseCommonsComponent(
                        FirebaseCommonsComponent.getInstance())
                    .setUtilsComponent(
                        UtilsComponent.getInstance())
                    .build()*/

            //return instance!!
        }
    }

    fun getVerificationHelper(): VerificationHelper
    fun getRegistrationManager(): RegistrationManager

    /*@Subcomponent.Builder
    interface Builder{
        fun setUtilsComponent(@BindsInstance utilsComponent: UtilsComponent):Builder
        fun setFirebaseCommonsComponent(@BindsInstance fbcComponent: FirebaseCommonsComponent): Builder
        fun setCryptoComponent(@BindsInstance cComponent: CryptoComponent):Builder
        fun build(): AuthActivityScopedComponent
    }*/
}
