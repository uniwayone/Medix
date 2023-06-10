package app.slyworks.base_feature

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import androidx.core.content.ContextCompat
import app.slyworks.base_feature.broadcast_receivers.CloudMessageBroadcastReceiver
import app.slyworks.base_feature.broadcast_receivers.VideoCallRequestBroadcastReceiver
import app.slyworks.base_feature.broadcast_receivers.VoiceCallRequestBroadcastReceiver
import app.slyworks.constants_lib.*
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.navigation_feature.Navigator


/**
 *Created by Joshua Sylvanus, 11:45 PM, 1/11/2022.
 */
class NotificationHelper(private val context:Context){
    //region Vars
    private val channelID1:String = context.getString(R.string.notification_channel_1_id)
    private val channelID2:String = context.getString(R.string.notification_channel_2_id)
    private val color:Int = ContextCompat.getColor(context, R.color.appBlue)
    //endregion

    companion object{
        //region Vars
        @SuppressLint("StaticFieldLeak")
        private lateinit var _context:Context
        //endregion

        private val notificationManager:NotificationManager by lazy(LazyThreadSafetyMode.NONE) {
            _context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        fun cancelNotification(notificationID: Int):Unit = notificationManager.cancel(notificationID)

        fun createAppServiceNotification(): Notification {
            val channelID1:String = _context.getString(R.string.notification_channel_1_id)
            val channelID2:String = _context.getString(R.string.notification_channel_2_id)
            val color:Int = ContextCompat.getColor(_context, R.color.appBlue)

            val builder:NotificationCompat.Builder = NotificationCompat.Builder(_context, channelID1)

            builder.setSmallIcon(R.drawable.splash_image_2)
                .setChannelId(channelID1)
                .setContentText("Medix is doing work in the background")
                .setColor(color)
                .setWhen(System.currentTimeMillis())
                .setAllowSystemGeneratedContextualActions(false)
                .setVisibility(VISIBILITY_PUBLIC)

            return builder.build()
        }
    }

    init { _context = context }

    fun createConsultationResponseNotification(fromUID:String,
                                               toUID:String,
                                               message:String,
                                               status:String,
                                               fullName:String){
        val intent:Intent = Navigator.intentFromIntentFilter(context, VIEW_REQUESTS_ACTIVITY_INTENT_FILTER)
            .apply {
            val b:Bundle = Bundle().apply {
                putString(EXTRA_CLOUD_MESSAGE_FROM_UID, fromUID)
                putString(EXTRA_CLOUD_MESSAGE_TO_UID, toUID)
                putString(EXTRA_CLOUD_MESSAGE_STATUS, status)
                putString(EXTRA_CLOUD_MESSAGE_FULLNAME, fullName)
            }
            putExtra(EXTRA_ACTIVITY, b)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            notificationManager.cancel(NOTIFICATION_CONSULTATION_REQUEST_RESPONSE)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_CANCEL_CURRENT)

        val builder:NotificationCompat.Builder = NotificationCompat.Builder(context, channelID2)

        builder.setSmallIcon(R.drawable.splash_image_2)
            .setChannelId(channelID2)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle("Consultation Request Response")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setColor(color)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setVisibility(VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)


        notificationManager.notify(NOTIFICATION_CONSULTATION_REQUEST_RESPONSE, builder.build())
    }

    fun createConsultationRequestNotification(fromUID:String,
                                              toFCMRegistrationToken:String,
                                              fullName:String,
                                              message:String){

        val intent:Intent = Navigator.intentFromIntentFilter(context, VIEW_REQUESTS_ACTIVITY_INTENT_FILTER)
            .apply {
                val b:Bundle = Bundle().apply {
                putString(EXTRA_CLOUD_MESSAGE_FROM_UID, fromUID)
                putString(EXTRA_CLOUD_MESSAGE_STATUS, REQUEST_PENDING)
                putString(EXTRA_CLOUD_MESSAGE_FULLNAME, fullName)
                putString(EXTRA_CLOUD_MESSAGE_TO_FCMTOKEN, toFCMRegistrationToken)
            }
            putExtra(EXTRA_ACTIVITY, b)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            notificationManager.cancel(NOTIFICATION_CONSULTATION_REQUEST)
        }


        val intentAccept = Intent(context, CloudMessageBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_CLOUD_MESSAGE_FROM_UID, fromUID)
            putExtra(EXTRA_CLOUD_MESSAGE_TYPE_ACCEPT, REQUEST_ACCEPTED)
            putExtra(EXTRA_CLOUD_MESSAGE_FULLNAME, fullName)
            putExtra(EXTRA_CLOUD_MESSAGE_TO_FCMTOKEN, toFCMRegistrationToken)
            putExtra(EXTRA_NOTIFICATION_IDENTIFIER, NOTIFICATION_CONSULTATION_REQUEST)

            setAction("app.slyworks.medix.BROADCAST_CLOUD_MESSAGE")

            notificationManager.cancel(NOTIFICATION_CONSULTATION_REQUEST)
        }

        val intentDecline = Intent(context, CloudMessageBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_CLOUD_MESSAGE_FROM_UID, fromUID)
            putExtra(EXTRA_CLOUD_MESSAGE_TYPE_DECLINE, REQUEST_DECLINED)
            putExtra(EXTRA_CLOUD_MESSAGE_FULLNAME, fullName)
            putExtra(EXTRA_CLOUD_MESSAGE_TO_FCMTOKEN, toFCMRegistrationToken)
            putExtra(EXTRA_NOTIFICATION_IDENTIFIER, NOTIFICATION_CONSULTATION_REQUEST)

            setAction("app.slyworks.medix.BROADCAST_CLOUD_MESSAGE")

            notificationManager.cancel(NOTIFICATION_CONSULTATION_REQUEST)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 100, intent,  PendingIntent.FLAG_CANCEL_CURRENT)
        val pendingIntentAccept:PendingIntent = PendingIntent.getBroadcast(context, 1, intentAccept, PendingIntent.FLAG_CANCEL_CURRENT)
        val pendingIntentDecline:PendingIntent = PendingIntent.getBroadcast(context, 2, intentDecline, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder:NotificationCompat.Builder = NotificationCompat.Builder(context, channelID2)

        builder.setSmallIcon(R.drawable.splash_image_2)
               .setChannelId(channelID2)
               .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
               .setContentTitle("Consultation Request")
               .setStyle(NotificationCompat.BigTextStyle()
                                           .bigText(message))
               .setColor(color)
               .setAutoCancel(true)
               .setWhen(System.currentTimeMillis())
               .setVisibility(VISIBILITY_PUBLIC)
               .setContentIntent(pendingIntent)
               .addAction(R.drawable.ic_check2,"Accept Request",pendingIntentAccept)
               .addAction(R.drawable.ic_close2,"Decline Request",pendingIntentDecline)


        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        notificationManager.notify(NOTIFICATION_CONSULTATION_REQUEST, notification)
    }

    fun createNewMessageNotification(message: MessageVModel, profileImage:Bitmap){
        val intent:Intent = Navigator.intentFromIntentFilter(context, MAIN_ACTIVITY_INTENT_FILTER).apply {
            putExtra(EXTRA_MAIN_FRAGMENT, FRAGMENT_CHAT_HOST)
            putExtra(EXTRA_RECEIVED_MESSAGE, message)

            notificationManager.cancel(NOTIFICATION_NEW_MESSAGE)
        }

        val pendingIntent:PendingIntent = PendingIntent.getActivity(context, 8,
        intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val remoteView:RemoteViews = RemoteViews(context.packageName, R.layout.layout_message_heads_up)
        remoteView.setTextViewText(R.id.tvTitle_notification_heads_up, message.senderFullName)
        remoteView.setTextViewText(R.id.tvText_notification_heads_up, message.content)

        val builder:NotificationCompat.Builder = NotificationCompat.Builder(context, channelID2)

        builder.setSmallIcon(R.drawable.splash_image_2)
            .setChannelId(channelID2)
            .setLargeIcon(profileImage)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(message.senderFullName)
            .setContentText(message.content)
            .setCustomHeadsUpContentView(remoteView)
            .setAutoCancel(true)
            .setColor(color)
            .setWhen(System.currentTimeMillis())
            .setVisibility(VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_NEW_MESSAGE, builder.build())
    }

    fun createReceivedMessageNotification(){
        val intent:Intent = Navigator.intentFromIntentFilter(context, MAIN_ACTIVITY_INTENT_FILTER).apply {
            putExtra(EXTRA_MAIN_FRAGMENT, FRAGMENT_CHAT_HOST)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            notificationManager.cancel(NOTIFICATION_NEW_MESSAGE)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 8,
            intent, PendingIntent.FLAG_CANCEL_CURRENT )

        val remoteView:RemoteViews = RemoteViews(context.packageName, R.layout.layout_message_heads_up)
        remoteView.setTextViewText(R.id.tvTitle_notification_heads_up, context.resources.getString(R.string.new_message_received))
        remoteView.setTextViewText(R.id.tvText_notification_heads_up, context.resources.getString(R.string.new_message_text))

        val builder:NotificationCompat.Builder = NotificationCompat.Builder(context, channelID2)

        builder.setSmallIcon(R.drawable.splash_image_2)
            .setChannelId(channelID2)
           /* .setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.splash_image_2))*/
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle("New Message Received")
            .setContentText("You have new messages. Tap to open")
            .setCustomHeadsUpContentView(remoteView)
            .setAutoCancel(true)
            .setColor(color)
            .setWhen(System.currentTimeMillis())
            .setVisibility(VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_NEW_MESSAGE, builder.build())
    }

    fun createIncomingVideoCallNotification(userDetails: FBUserDetailsVModel){
        val intent = Navigator.intentFromIntentFilter(context, VIDEOCALL_ACTIVITY_INTENT_FILTER).apply {
            val b:Bundle = Bundle().apply {
                putString(EXTRA_VIDEO_CALL_TYPE, VIDEO_CALL_INCOMING)
                putParcelable(EXTRA_VIDEO_CALL_USER_DETAILS, userDetails)
            }

            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            notificationManager.cancel(NOTIFICATION_VIDEO_CALL_REQUEST)
        }

        val intentDecline = Intent(context, VideoCallRequestBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_INCOMING_VIDEO_CALL_FROM_UID, userDetails.firebaseUID)
            setAction("app.slyworks.medix.BROADCAST_VIDEO_CALL_REQUEST")
            putExtra(EXTRA_INCOMING_VIDEO_CALL_RESPONSE_TYPE, REQUEST_DECLINED)

            notificationManager.cancel(NOTIFICATION_VIDEO_CALL_REQUEST)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_CANCEL_CURRENT )

        //val pendingIntentAccept:PendingIntent = PendingIntent.getActivity(context, 6, intentAccept, PendingIntent.FLAG_CANCEL_CURRENT)
        val pendingIntentDecline:PendingIntent = PendingIntent.getBroadcast(context, 7, intentDecline,  PendingIntent.FLAG_CANCEL_CURRENT)

        val remoteView:RemoteViews = RemoteViews(context.packageName, R.layout.layout_message_heads_up)
        remoteView.setTextViewText(R.id.tvTitle_notification_heads_up, "Incoming Video Call")
        remoteView.setTextViewText(R.id.tvText_notification_heads_up, "You have an incoming Video Call. Tap to answer")


        val builder:NotificationCompat.Builder = NotificationCompat.Builder(context, channelID2)

        builder.setSmallIcon(R.drawable.splash_image_2)
            .setChannelId(channelID2)
            .setPriority(PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            //{ delay, vibrate, sleep, vibrate, sleep } pattern???
            .setVibrate(longArrayOf(1_000, 1_000, 1_000, 1_000, 1_000))
            .setContentTitle("Incoming Video Call")
            .setContentText("${userDetails.fullName} would like a video call with you")
            .setCustomHeadsUpContentView(remoteView)
            .setColor(color)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setWhen(System.currentTimeMillis())
            .setVisibility(VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_check2,"Accept",pendingIntent)
            .addAction(R.drawable.ic_close2,"Decline",pendingIntentDecline)


        notificationManager.notify(NOTIFICATION_VIDEO_CALL_REQUEST, builder.build())
    }

    fun createIncomingVoiceCallNotification(details: FBUserDetailsVModel, bitmap: Bitmap) {
        /*TODO:create notification with custom layout*/
        val intent = Navigator.intentFromIntentFilter(context, VOICECALL_ACTIVITY_INTENT_FILTER).apply {
            val b:Bundle = Bundle().apply {
                putString(EXTRA_VOICE_CALL_TYPE, VOICE_CALL_INCOMING)
                putParcelable(EXTRA_VOICE_CALL_USER_DETAILS, details)
            }

            putExtra(EXTRA_ACTIVITY, b)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            notificationManager.cancel(NOTIFICATION_VOICE_CALL_REQUEST)
        }

        val intentDecline = Intent(context, VoiceCallRequestBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_INCOMING_VOICE_CALL_FROM_UID, details.firebaseUID)
            setAction("app.slyworks.medix.BROADCAST_VOICE_CALL_REQUEST")
            putExtra(EXTRA_INCOMING_VOICE_CALL_RESPONSE_TYPE, REQUEST_DECLINED)

            notificationManager.cancel(NOTIFICATION_VOICE_CALL_REQUEST)
        }

        val pendingIntent:PendingIntent = PendingIntent.getActivity(context, 9,
            intent, PendingIntent.FLAG_CANCEL_CURRENT )
        val pendingIntentDecline:PendingIntent = PendingIntent.getBroadcast(context, 11,
            intentDecline, PendingIntent.FLAG_CANCEL_CURRENT)

        val remoteView:RemoteViews = RemoteViews(context.packageName, R.layout.layout_message_heads_up)
        remoteView.setTextViewText(R.id.tvTitle_notification_heads_up, "Incoming Voice Call")
        remoteView.setTextViewText(R.id.tvText_notification_heads_up, "You have an incoming Voice Call. Tap to answer")

        val builder:NotificationCompat.Builder = NotificationCompat.Builder(context, channelID2)

        builder.setSmallIcon(R.drawable.splash_image_2)
            .setChannelId(channelID2)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1_000, 1_000, 1_000, 1_000, 1_000))
            .setContentTitle("Incoming Voice Call")
            .setContentText("${details.fullName} would like a voice call with you")
            .setCustomHeadsUpContentView(remoteView)
            .setLargeIcon(bitmap)
            .setColor(color)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setVisibility(VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_check2, "Accept", pendingIntent)
            .addAction(R.drawable.ic_close2, "Decline", pendingIntentDecline)

        notificationManager.notify(NOTIFICATION_VOICE_CALL_REQUEST, builder.build())
    }


}