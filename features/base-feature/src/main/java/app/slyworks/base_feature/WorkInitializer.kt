package app.slyworks.base_feature

import android.content.Context
import androidx.work.*
import app.slyworks.base_feature.workers.FCMTokenUploadWorker
import app.slyworks.base_feature.workers.MessageWorker
import app.slyworks.base_feature.workers.ProfileUpdateWorker
import app.slyworks.base_feature.workers.StartServiceWorker
import app.slyworks.constants_lib.KEY_FCM_UPLOAD_TOKEN


/**
 * Created by Joshua Sylvanus, 9:37 PM, 28/11/2022.
 */
class WorkInitializer(private val context:Context) {

    fun initStartServiceWork(){
        val startServiceWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<StartServiceWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "StartServiceWorker",
                ExistingWorkPolicy.KEEP,
                startServiceWorkRequest)
    }

    fun initFCMTokenUploadWork(token:String){
        val data = Data.Builder()
            .putString(KEY_FCM_UPLOAD_TOKEN, token)
            .build()

        val fcmUploadWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<FCMTokenUploadWorker>()
                .setInputData(data)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "FCMTokenUploadWorker",
                ExistingWorkPolicy.REPLACE,
                fcmUploadWorkRequest)
    }

    fun initMessageWork(){
        /*re-queued in onStop() of MessageWorker*/
        val messageWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<MessageWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "MessageWorker",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                messageWorkRequest)
    }

    fun initProfileUpdateWork(){
        /*TODO:would be set from settings*/
        val profileUpdateWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ProfileUpdateWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "ProfileUpdateWorker",
                ExistingWorkPolicy.REPLACE,
                profileUpdateWorkRequest)
    }
}