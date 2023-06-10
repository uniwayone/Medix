package app.slyworks.crypto_lib.di

import app.slyworks.crypto_lib.CryptoConfig
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.CryptoLibScope
import dagger.Component


/**
 * Created by Joshua Sylvanus, 7:36 PM, 02/12/2022.
 */
@Component(modules = [CryptoModule::class])
@CryptoLibScope
interface CryptoComponent {
    companion object{
        private var instance:CryptoComponent? = null

        @JvmStatic
        fun getInstance():CryptoComponent{
            if(instance == null)
                instance =
                DaggerCryptoComponent.create()

            return instance!!
        }
    }

    fun getCryptoHelper(): CryptoHelper
}
