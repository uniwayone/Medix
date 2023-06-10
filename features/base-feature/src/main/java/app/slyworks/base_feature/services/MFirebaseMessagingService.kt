package app.slyworks.base_feature.services

import android.graphics.Bitmap
import android.widget.Toast
import app.slyworks.auth_lib.LoginManager
import app.slyworks.auth_lib.UsersManager
import app.slyworks.base_feature.ActivityUtils
import app.slyworks.base_feature.NotificationHelper
import app.slyworks.base_feature.WorkInitializer
import app.slyworks.base_feature.di.BaseFeatureComponent
import app.slyworks.base_feature.di.MFirebaseMSComponent
import app.slyworks.constants_lib.*
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.utils_lib.PreferenceManager
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import java.lang.Exception
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 10:09 PM, 1/11/2022.
 */
class MFirebaseMessagingService : FirebaseMessagingService(){
    //region Vars
      @Inject
      lateinit var preferenceManager: PreferenceManager
      @Inject
      lateinit var loginManager: LoginManager
      @Inject
      lateinit var usersManager: UsersManager
      @Inject
      lateinit var notificationHelper: NotificationHelper
      @Inject
      lateinit var dataManager: DataManager
      @Inject
      lateinit var networkRegister: NetworkRegister
      @Inject
      lateinit var workInitializer: WorkInitializer
    //endregion

      init {
         MFirebaseMSComponent.getInitialBuilder()
             .build()
             .inject(this)
      }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        GlobalScope.launch(Dispatchers.IO) {
            preferenceManager.set(KEY_IS_THERE_NEW_FCM_REG_TOKEN, true)
            preferenceManager.set(KEY_FCM_REGISTRATION, token)

            if(networkRegister.getNetworkStatus() && loginManager.getLoginStatus())
                usersManager.sendFCMTokenToServer(token)
            else
                 /*enqueue task for upload since there is no network connection or user is not logged in */
                workInitializer.initFCMTokenUploadWork(token)

            cancel()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
       if(ActivityUtils.isAppInForeground())
            return

       when(getMessageType(remoteMessage)){
           FCM_NEW_MESSAGE -> {
               val message: MessageVModel =
                   MessageVModel(
                       type = remoteMessage.data["type"]!!,
                       fromUID = remoteMessage.data["from_uid"]!!,
                       toUID = remoteMessage.data["to_uid"]!!,
                       senderFullName = remoteMessage.data["sender_fullname"]!!,
                       receiverFullName = remoteMessage.data["receiver_fullname"]!!,
                       content = remoteMessage.data["content"]!!,
                       timeStamp = remoteMessage.data["time_stamp"]!!,
                       messageID = remoteMessage.data["message_id"]!!,
                       status = remoteMessage.data["status"]!!.toDouble(),
                       senderImageUri = remoteMessage.data["sender_image_uri"]!!,
                       accountType = remoteMessage.data["account_type"]!!,
                       FCMRegistrationToken = remoteMessage.data["sender_fcm_registration_token"]!!,
                       receiverImageUri = remoteMessage.data["receiver_image_uri"]!!
                   )

               CoroutineScope(Dispatchers.IO).launch {
                   val bitmap: Bitmap = Glide.with(this@MFirebaseMessagingService.applicationContext)
                       .asBitmap()
                       .load(message.senderImageUri)
                       .submit()
                       .get()

                  notificationHelper.createNewMessageNotification(message, bitmap)
               }
           }

           FCM_REQUEST ->{
             val fromUID:String = remoteMessage.data["fromUID"]!!
             val toFCMRegistrationToken:String = remoteMessage.data["toFCMRegistrationToken"]!!
             val fullName:String = remoteMessage.data["fullName"]!!
             val message:String = remoteMessage.data["message"]!!
             notificationHelper.createConsultationRequestNotification( fromUID, fullName, message, toFCMRegistrationToken)
           }

           FCM_RESPONSE_ACCEPTED, FCM_RESPONSE_DECLINED ->{
               val fromUID:String = remoteMessage.data["fromUID"]!!
               val toUID:String = dataManager.getUserDetailsParam<String>("firebaseUID")!!
               val message:String = remoteMessage.data["message"]!!
               val status:String = remoteMessage.data["status"]!!
               val fullName:String = remoteMessage.data["fullName"]!!
               notificationHelper.createConsultationResponseNotification(fromUID, toUID, message, status, fullName)
           }

           FCM_VOICE_CALL_REQUEST ->{
              val details: FBUserDetailsVModel = FBUserDetailsVModel(
                  accountType = remoteMessage.data["accountType"]!!,
                  firstName = remoteMessage.data["firstName"]!!,
                  lastName = remoteMessage.data["lastName"]!!,
                  fullName = remoteMessage.data["fullName"]!!,
                  email = remoteMessage.data["email"]!!,
                  sex = remoteMessage.data["sex"]!!,
                  age = remoteMessage.data["age"]!!,
                  firebaseUID = remoteMessage.data["firebaseUID"]!!,
                  agoraUID = remoteMessage.data["agoraUID"]!!,
                  FCMRegistrationToken = remoteMessage.data["fcmRegistrationToken"]!!,
                  imageUri = remoteMessage.data["imageUri"]!!,
                  history = null,
                  specialization = null
              )

               CoroutineScope(Dispatchers.IO).launch {
                 val bitmap: Bitmap = Glide.with(this@MFirebaseMessagingService.applicationContext)
                     .asBitmap()
                     .load(details.imageUri)
                     .submit()
                     .get()

                 notificationHelper.createIncomingVoiceCallNotification(details, bitmap)
               }
           }
           FCM_NEW_UPDATE_MESSAGE ->{
               /*TODO:retrieve new Url to download new version of app from*/
           }
       }
    }

    private fun getMessageType(remoteMessage:RemoteMessage):String =
      remoteMessage.data.get("type")!!

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)

        Toast.makeText(this.applicationContext,"cloud message sent", Toast.LENGTH_LONG).show()
    }

    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)

        Toast.makeText(this.applicationContext,"cloud message not sent", Toast.LENGTH_LONG).show()
    }
}