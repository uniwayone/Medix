package app.slyworks.fcm_api_lib.di

import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.FCM_APILibScope
import app.slyworks.fcm_api_lib.FCMClientApi
import dagger.Component


/**
 * Created by Joshua Sylvanus, 8:01 PM,  8:01 PM, 02-Dec-2022.
 */
@Component(modules = [FCM_APIModule::class])
@FCM_APILibScope
interface FCM_APIComponent {
    companion object{
        private var instance:FCM_APIComponent? = null

        @JvmStatic
        fun getInstance():FCM_APIComponent{
            if(instance == null)
                instance =
                DaggerFCM_APIComponent.create()

            return instance!!
        }
    }

    fun getFCMClientApi(): FCMClientApi
}
