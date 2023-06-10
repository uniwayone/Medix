package app.slyworks.base_feature.services

import android.app.Service
import android.content.Intent
import android.os.IBinder


/**
 *Created by Joshua Sylvanus, 12:03 PM, 13/05/2022.
 */
class BackgroundService(): Service() {
    //region Vars
    //endregion

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
       /*TODO:shouldnt return null cause a component would bind to it*/
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        /*should start ListenerService before being destroyed */
        super.onDestroy()
    }

}