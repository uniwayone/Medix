package app.slyworks.network_lib

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


internal class NetworkWatcherImpl(private val context: Context) : NetworkWatcher {
    //region Vars
    private var mCm:ConnectivityManager
    private var mConnectivityCallback: ConnectivityCallback? = null
    private var mNetworkRequest: NetworkRequest? = null
    private var mO:PublishSubject<Boolean>? = PublishSubject.create()
    //endregion

    init{
        mNetworkRequest =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

        mCm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getNetworkStatus(): Boolean {
        val network: Network = mCm.activeNetwork ?: return false
        val capabilities = mCm.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    @SuppressLint("NewApi")
    override fun subscribeTo(): Observable<Boolean> {
        mConnectivityCallback = ConnectivityCallback(mO!!)
        mCm.registerNetworkCallback(mNetworkRequest!!, mConnectivityCallback!!)

        /*calling it first time*/
        return mO!!.startWithItem(getNetworkStatus()).hide()

    }

    override fun dispose() {
        if(mConnectivityCallback != null)
           mCm.unregisterNetworkCallback(mConnectivityCallback!!)

        mO = null
        mConnectivityCallback = null
    }
}

