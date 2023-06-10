package app.slyworks.fcm_api_lib.di

import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.FCM_APILibScope
import app.slyworks.fcm_api_lib.ApiClient
import app.slyworks.fcm_api_lib.FCMClientApi
import dagger.Module
import dagger.Provides


/**
 *Created by Joshua Sylvanus, 8:52 PM, 23/07/2022.
 */

@Module
object FCM_APIModule {
    @Provides
    @FCM_APILibScope
    fun provideApiClient(): FCMClientApi =
        ApiClient.getApiInterface()
}