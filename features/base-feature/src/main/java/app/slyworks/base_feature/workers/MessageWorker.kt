package app.slyworks.base_feature.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.slyworks.communication_lib.MessageManager
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.MessageVModel
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 11:10 AM, 16/05/2022.
 */
class MessageWorker(private val context: Context,
                    params:WorkerParameters) : CoroutineWorker(context, params) {
    //region Vars
    @Inject
    lateinit var dataManager: DataManager
    @Inject
    lateinit var messageManager: MessageManager
    //endregion

    init{
        /*(applicationContext as App).appComponent
            .workerComponentBuilder()
            .build()
            .inject(this)*/
    }

    override suspend fun doWork(): Result {
        val l:List<MessageVModel> = dataManager.getUnsentMessages()

        l.forEach {
          messageManager.sendMessage(it)
                        .subscribe()
        }

         return Result.success()
    }

}