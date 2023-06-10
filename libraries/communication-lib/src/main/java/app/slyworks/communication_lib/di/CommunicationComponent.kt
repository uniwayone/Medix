package app.slyworks.communication_lib.di

import app.slyworks.communication_lib.*
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.CommunicationLibScope
import app.slyworks.fcm_api_lib.FCMClientApi
import app.slyworks.fcm_api_lib.di.FCM_APIComponent
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.firebase_commons_lib.di.FirebaseCommonsComponent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 8:58 PM, 02-Dec-2022.
 */
@Component(modules = [CommunicationModule::class])
@CommunicationLibScope
interface CommunicationComponent {
    companion object{
        private var instance:CommunicationComponent? = null

        @JvmStatic
        fun getInstance():CommunicationComponent{
            if(instance == null)
               instance =
               DaggerCommunicationComponent.builder()
                   .setDataComponent(DataComponent.getInstance())
                   .setFcmApiComponent(FCM_APIComponent.getInstance())
                   .setFirebaseCommonsComponent(
                       FirebaseCommonsComponent.getInstance())
                   .build()

            return instance!!
        }
    }

    fun getCallManager(): CallManager
    fun provideMessageManager(): MessageManager
    fun provideCallHistoryManager(): CallHistoryManager
    fun getConsultationRequestsManager(): ConsultationRequestsManager
    fun getConnectionStatusManager(): ConnectionStatusManager

    @Component.Builder
    interface Builder{
        fun setFcmApiComponent(@BindsInstance component: FCM_APIComponent):Builder
        fun setFirebaseCommonsComponent(@BindsInstance component: FirebaseCommonsComponent):Builder
        fun setDataComponent(@BindsInstance component: DataComponent):Builder
        fun build(): CommunicationComponent
    }
}
