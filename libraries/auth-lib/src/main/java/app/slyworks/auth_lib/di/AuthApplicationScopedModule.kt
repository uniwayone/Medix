package app.slyworks.auth_lib.di

import com.google.firebase.auth.FirebaseAuth
import app.slyworks.auth_lib.LoginManager
import app.slyworks.auth_lib.MAuthStateListener
import app.slyworks.auth_lib.PersonsManager
import app.slyworks.auth_lib.UsersManager
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.crypto_lib.di.CryptoComponent
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.data_lib.di.DataModule
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.AuthLibScope
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsComponent
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsModule
import app.slyworks.utils_lib.PreferenceManager
import app.slyworks.utils_lib.TimeHelper
import app.slyworks.utils_lib.di.UtilsComponent
import app.slyworks.utils_lib.di.UtilsModule
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 9:48 PM, 11/08/2022.
 */
@Module
object AuthApplicationScopedModule {
    @Provides
    @AuthLibScope
    fun provideUsersManager(fbcComponent: FirebaseCommonsComponent,
                            dComponent: DataComponent): UsersManager {
        return UsersManager(
            fbcComponent.getFirebaseUtils(),
            dComponent.getDataManager())
    }

    @Provides
    @AuthLibScope
    fun providePersonsManager(dComponent: DataComponent): PersonsManager =
        PersonsManager(dComponent.getDataManager())

    @Provides
    @AuthLibScope
    fun provideLoginManager(uComponent:UtilsComponent,
                            fbcComponent: FirebaseCommonsComponent,
                            cComponent: CryptoComponent,
                            usersManager: UsersManager,
                            dComponent: DataComponent,
                            authStateListener:MAuthStateListener): LoginManager =
        LoginManager(
            uComponent.getPreferenceManager(),
            fbcComponent.getFirebaseAuth(),
            usersManager,
            fbcComponent.getFirebaseUtils(),
            uComponent.getTimeHelper(),
            cComponent.getCryptoHelper(),
            dComponent.getDataManager(),
            authStateListener);

}