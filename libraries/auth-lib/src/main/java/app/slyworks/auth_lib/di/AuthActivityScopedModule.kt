package app.slyworks.auth_lib.di

import app.slyworks.auth_lib.MAuthStateListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import app.slyworks.auth_lib.RegistrationManager
import app.slyworks.auth_lib.VerificationHelper
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.crypto_lib.di.CryptoComponent
import app.slyworks.crypto_lib.di.CryptoModule
import app.slyworks.di_base_lib.ActivityScope
import app.slyworks.di_base_lib.AuthLibActivityScope
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsComponent
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsModule
import app.slyworks.utils_lib.TaskManager
import app.slyworks.utils_lib.di.UtilsComponent
import app.slyworks.utils_lib.di.UtilsModule
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 9:37 PM, 23/07/2022.
 */

@Module
object AuthActivityScopedModule {
    @Provides
    @AuthLibActivityScope
    fun provideVerificationHelper(authStateListener: MAuthStateListener,
                                  fbcComponent: FirebaseCommonsComponent,
                                  cryptoComponent: CryptoComponent):VerificationHelper =
        VerificationHelper(authStateListener,
                           fbcComponent.getFirebaseAuth(),
                           fbcComponent.getFirebaseUtils(),
                           cryptoComponent.getCryptoHelper())

    @Provides
    @AuthLibActivityScope
    fun provideRegistrationManager(fbcComponent: FirebaseCommonsComponent,
                                   utilsComponent: UtilsComponent,
                                   cComponent:CryptoComponent,
                                   authStateListener: MAuthStateListener): RegistrationManager =
        RegistrationManager(fbcComponent.getFirebaseAuth(),
                            fbcComponent.getFirebaseMessaging(),
                            fbcComponent.getFirebaseUtils(),
                            utilsComponent.getTaskManager(),
                            cComponent.getCryptoHelper(),
                            authStateListener)


}