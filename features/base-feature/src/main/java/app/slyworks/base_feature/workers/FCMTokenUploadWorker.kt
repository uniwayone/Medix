package app.slyworks.base_feature.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.slyworks.auth_lib.UsersManager
import app.slyworks.constants_lib.KEY_FCM_UPLOAD_TOKEN
import app.slyworks.data_lib.DataManager
import app.slyworks.firebase_commons_lib.FirebaseUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 10:47 AM, 16/05/2022.
 */
class FCMTokenUploadWorker(private val context: Context,
                           params:WorkerParameters) : CoroutineWorker(context, params) {
    //region Vars
    @Inject
    lateinit var firebaseUtils: FirebaseUtils
    @Inject
    lateinit var dataManager: DataManager
    //endregion

    init{
        /*(applicationContext as App).appComponent
            .workerComponentBuilder()
            .build()
            .inject(this)*/
    }


    override suspend fun doWork(): Result {
        /*ensure there is a signed in use first*/
        val token:String = inputData.getString(KEY_FCM_UPLOAD_TOKEN) ?: return Result.failure()
        //val UID:String = _job.await() ?: return Result.failure()
        val UID:String = dataManager.getUserDetailsParam<String>("firebaseUID") ?: return Result.success()

        var r: Result = Result.retry()
        firebaseUtils.getUserDataRefForWorkManager(UID)
            .setValue(token)
            .addOnCompleteListener {
                if (it.isSuccessful)
                   r = Result.success()
                else
                   r = Result.retry()
            }.await()

        return r
    }


}