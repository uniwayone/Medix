package app.slyworks.base_feature.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import app.slyworks.base_feature.ActivityUtils
import app.slyworks.base_feature.ListenerManager
import app.slyworks.base_feature.NotificationHelper
import app.slyworks.base_feature.WorkInitializer
import app.slyworks.constants_lib.APP_SERVICE_ID
import javax.inject.Inject


/**
 *Created by Joshua Sylvanus, 12:03 PM, 1/21/2022.
 */

class ListenerService : Service() {
    //region Vars
    @Inject
    @JvmField
    var listenerManager: ListenerManager? = null

    @Inject
    lateinit var workInitializer: WorkInitializer

    private var shouldWorkerBeReInitialized:Boolean = false
    //endregion

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(APP_SERVICE_ID, NotificationHelper.createAppServiceNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(ActivityUtils.isAppInForeground()){
            stopSelf()
            shouldWorkerBeReInitialized = false
            return START_NOT_STICKY
        }

        /*initializing class for listening to network events*/
        //mListenerManager = ListenerManager()
        listenerManager!!.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        if(listenerManager != null) {
            listenerManager!!.stop()
            listenerManager = null
        }

        /*re-queueing Work for WorkManager*/
        if(shouldWorkerBeReInitialized)
           workInitializer.initStartServiceWork()
    }
}