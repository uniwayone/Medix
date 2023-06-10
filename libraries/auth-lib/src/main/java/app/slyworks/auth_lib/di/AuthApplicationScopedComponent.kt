package app.slyworks.auth_lib.di

import app.slyworks.auth_lib.LoginManager
import app.slyworks.auth_lib.MAuthStateListener
import app.slyworks.auth_lib.PersonsManager
import app.slyworks.auth_lib.UsersManager
import app.slyworks.crypto_lib.di.CryptoComponent
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.AuthLibScope
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsComponent
import app.slyworks.utils_lib.PreferenceManager
import app.slyworks.utils_lib.di.UtilsComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 8:48 PM, 02-Dec-2022.
 */
@Component(modules = [AuthApplicationScopedModule::class])
@AuthLibScope
interface AuthApplicationScopedComponent {
    companion object{
        private var instance:AuthApplicationScopedComponent? = null

        @JvmStatic
        fun getInstance():AuthApplicationScopedComponent{
            if(instance == null)
                instance =
                DaggerAuthApplicationScopedComponent.builder()
                    .setAuthStateListener(
                        MAuthStateListener(UtilsComponent.getInstance().getPreferenceManager()))
                    .setCryptoComponent(
                        CryptoComponent.getInstance())
                    .setDataComponent(
                        DataComponent.getInstance())
                    .setFBCComponent(
                        FirebaseCommonsComponent.getInstance())
                    .setUtilsComponent(
                        UtilsComponent.getInstance())
                    .build()

            return instance!!
        }
    }

    fun getUsersManager():UsersManager
    fun providePersonsManager(): PersonsManager
    fun getLoginManager(): LoginManager
    fun getAuthStateListener():MAuthStateListener

    fun authActivityComponent(): AuthActivityScopedComponent

    @Component.Builder
    interface Builder{
       fun setAuthStateListener(@BindsInstance listener: MAuthStateListener):Builder
       fun setFBCComponent(@BindsInstance fbcComponent: FirebaseCommonsComponent): Builder
       fun setUtilsComponent(@BindsInstance uComponent:UtilsComponent):Builder
       fun setCryptoComponent(@BindsInstance cCryptoComponent: CryptoComponent):Builder
       fun setDataComponent(@BindsInstance dComponent: DataComponent):Builder
       fun build(): AuthApplicationScopedComponent
    }
}
