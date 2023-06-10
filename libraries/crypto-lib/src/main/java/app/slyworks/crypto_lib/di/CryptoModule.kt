package app.slyworks.crypto_lib.di

import app.slyworks.crypto_lib.CryptoConfig
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.CryptoLibScope
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 9:25 PM, 22/11/2022.
 */

@Module
object CryptoModule {
    @Provides
    @CryptoLibScope
    fun provideCryptoHelper():CryptoHelper = CryptoHelper()
}