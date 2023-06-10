package app.slyworks.medix

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import app.slyworks.di_base_lib.AppComponent
import timber.log.Timber


/**
 * Created by Joshua Sylvanus, 3:59 PM, 12/10/2021.
 */


/*TODO:add dynamic feature module*/
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
        initTimber()
        //initExceptionHandler()
        //initStartServiceWork()
        //initStetho()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            initNotificationChannels()
    }

    private fun initDI():Unit =
       AppComponent.setContext(this)

    private fun initStetho(){
        if(!BuildConfig.DEBUG)
            return

       // Stetho.initializeWithDefaults(this)
    }

    private fun initTimber(){
        /* to ensure logging does not occur in RELEASE builds*
          the actual dependency is in :models */
        if(!BuildConfig.DEBUG)
            return

        Timber.plant(object: Timber.DebugTree(){
            override fun createStackElementTag(element: StackTraceElement): String {
                return String.format(
                    "%s:%s",
                    element.methodName,
                    super.createStackElementTag(element))
            }
        })
    }

    private fun initExceptionHandler():Unit =
        Thread.setDefaultUncaughtExceptionHandler(UniversalExceptionHandler(this))


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initNotificationChannels(){
        /*would be using ~2 notification channels, 1 for general app notifications, 1 for ForegroundService*/
        createNotificationChannel1()
        createNotificationChannel2()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel1(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val CHANNEL_ID = getString(R.string.notification_channel_1_id)
        val channelName = getString(R.string.notification_channel_1_name)
        val channelDescriptionText = getString(R.string.notification_channel_1_description)
        val channelImportance = NotificationManager.IMPORTANCE_HIGH

        val channel: NotificationChannel = NotificationChannel(CHANNEL_ID, channelName, channelImportance)
        channel.setDescription(channelDescriptionText)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel2(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val CHANNEL_ID = getString(R.string.notification_channel_2_id)
        val channelName = getString(R.string.notification_channel_2_name)
        val channelDescriptionText = getString(R.string.notification_channel_2_description)
        val channelImportance = NotificationManager.IMPORTANCE_HIGH

        val channel: NotificationChannel = NotificationChannel(CHANNEL_ID, channelName, channelImportance)
        channel.setDescription(channelDescriptionText)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}