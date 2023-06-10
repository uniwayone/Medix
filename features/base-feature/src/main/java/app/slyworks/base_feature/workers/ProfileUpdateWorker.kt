package app.slyworks.base_feature.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.slyworks.constants_lib.KEY_UPLOAD_USER_PROFILE
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.utils_lib.PreferenceManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 1:43 PM, 16/05/2022.
 */
class ProfileUpdateWorker(private var context: Context,
                          params:WorkerParameters) : CoroutineWorker(context,params) {
    //region Vars
    @Inject
    lateinit var preferenceManager: PreferenceManager
    @Inject
    lateinit var dataManager: DataManager
    @Inject
    lateinit var firebaseUtils: FirebaseUtils
    //endregion

    init{
        /*(applicationContext as App).appComponent
            .workerComponentBuilder()
            .build()
            .inject(this)*/
    }

    override suspend fun doWork(): Result {
       val isChanged:Boolean = preferenceManager.get(KEY_UPLOAD_USER_PROFILE, false) ?: return Result.failure()
       if(!isChanged)
           return Result.failure()

        var r:Result = Result.retry()

        val details: FBUserDetailsVModel? = dataManager.getUserDetailsParam<FBUserDetailsVModel>("userDetails")
        if(details == null)
            return Result.success()

        firebaseUtils.getUserDataForUIDRef(details.firebaseUID)
                .setValue(details)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        r = Result.success()
                    else {
                        r = Result.retry()
                    }
                }.await()

        return r
    }
}