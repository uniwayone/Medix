package app.slyworks.communication_lib.di

import app.slyworks.communication_lib.*
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.CommunicationLibScope
import com.google.firebase.database.FirebaseDatabase
import app.slyworks.fcm_api_lib.FCMClientApi
import app.slyworks.fcm_api_lib.di.FCM_APIComponent
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsComponent
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 5:57 AM, 09/08/2022.
 */
@Module
object CommunicationModule {
    @Provides
    @CommunicationLibScope
    fun provideCallManager(firebaseCommonsComponent: FirebaseCommonsComponent,
                           fcmApiComponent: FCM_APIComponent,
                           dataComponent: DataComponent)
    : CallManager =
        CallManager(
            firebaseCommonsComponent.getFirebaseDatabase(),
            fcmApiComponent.getFCMClientApi(),
            firebaseCommonsComponent.getFirebaseUtils(),
            dataComponent.getDataManager())

    @Provides
    @CommunicationLibScope
    fun provideMessageManager(firebaseCommonsComponent: FirebaseCommonsComponent,
                              fcmApiComponent: FCM_APIComponent,
                              dataComponent: DataComponent)
    : MessageManager =
        MessageManager(
            firebaseCommonsComponent.getFirebaseDatabase(),
            fcmApiComponent.getFCMClientApi(),
            firebaseCommonsComponent.getFirebaseUtils(),
            dataComponent.getDataManager())

    @Provides
    @CommunicationLibScope
    fun provideCallHistoryManager(firebaseCommonsComponent: FirebaseCommonsComponent,
                                  dataComponent: DataComponent)
    : CallHistoryManager =
        CallHistoryManager(
            firebaseCommonsComponent.getFirebaseDatabase(),
            dataComponent.getDataManager())


    @Provides
    @CommunicationLibScope
    fun provideConsultationRequestsManager(firebaseCommonsComponent: FirebaseCommonsComponent,
                                           fcmApiComponent: FCM_APIComponent,
                                           dataComponent: DataComponent)
    : ConsultationRequestsManager =
        ConsultationRequestsManager(
            firebaseCommonsComponent.getFirebaseDatabase(),
            fcmApiComponent.getFCMClientApi(),
            firebaseCommonsComponent.getFirebaseUtils(),
            dataComponent.getDataManager())

    @Provides
    @CommunicationLibScope
    fun provideConnectionStatusManager(firebaseCommonsComponent: FirebaseCommonsComponent,
                                       dataComponent: DataComponent)
    : ConnectionStatusManager =
        ConnectionStatusManager(
            firebaseCommonsComponent.getFirebaseDatabase(),
            dataComponent.getDataManager())
}