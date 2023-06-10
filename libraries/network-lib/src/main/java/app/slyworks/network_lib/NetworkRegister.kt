package app.slyworks.network_lib

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import io.reactivex.rxjava3.core.Observable


/**
 * Created by Joshua Sylvanus, 11:27 AM, 29/05/2022.
 */
@SuppressLint("NewApi")
class NetworkRegister(private val context: Context ) {
    //region Vars
    private var impl: NetworkWatcher? = null
    //endregion

   init{ init2() }

    private fun init1(context: Context){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            val  filter = IntentFilter()
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            impl = NetworkBroadcastReceiver()
            context.registerReceiver(impl as NetworkBroadcastReceiver, filter)
        }else{
            impl = NetworkWatcherImpl(context)
        }
    }

    private fun init2(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            impl = NetworkWatcherLegacyImpl(context)
        else
            impl = NetworkWatcherImpl(context)
    }


    fun getNetworkStatus(): Boolean{
        if(impl == null)
            init2()

        return impl!!.getNetworkStatus()
    }

    fun subscribeToNetworkUpdates():Observable<Boolean>{
        if (impl == null)
           init2()

        return impl!!.subscribeTo()
    }

    fun unsubscribeToNetworkUpdates(){
        impl?.dispose()
        impl = null
    }

}