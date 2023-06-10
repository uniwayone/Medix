package app.slyworks.base_feature.di

import android.content.Context
import app.slyworks.base_feature.ListenerManager
import app.slyworks.base_feature.NotificationHelper
import app.slyworks.base_feature.VibrationManager
import app.slyworks.communication_lib.di.CommunicationComponent
import app.slyworks.di_base_lib.BaseFeatureScope
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 9:45 PM, 02-Dec-2022.
 */
@Module
object BaseFeatureModule {
    @Provides
    @BaseFeatureScope
    fun provideNotificationHelper(context: Context):NotificationHelper
    = NotificationHelper(context)

   @Provides
   @BaseFeatureScope
   fun provideListenerManager(context:Context,
                              cComponent:CommunicationComponent,
                              notificationHelper: NotificationHelper)
   :ListenerManager =
       ListenerManager(
           context,
           cComponent.getCallManager(),
           cComponent.getConsultationRequestsManager(),
           notificationHelper,
           cComponent.getConnectionStatusManager())

    @Provides
    @BaseFeatureScope
    fun provideVibrationManager(context: Context): VibrationManager =
        VibrationManager(context)
}