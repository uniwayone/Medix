package app.slyworks.network_lib

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

internal class NetworkWatcherLegacyImpl(
    private var context:Context): NetworkWatcher {
    //region Vars
    private var cm: ConnectivityManager
    private var connectivityCallback: ConnectivityCallback? = null
    private var networkRequest: NetworkRequest? = null
    private var o: PublishSubject<Boolean>? = PublishSubject.create()
    //endregion

    init {
     networkRequest =
         NetworkRequest.Builder()
             .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
             .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
             .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
             .build()

        cm =  context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    }

    @SuppressLint("MissingPermission")
    override fun getNetworkStatus(): Boolean {
        val networkInfo: NetworkInfo? =
            cm.getActiveNetworkInfo()

        return (networkInfo != null && networkInfo.isConnected)
    }

    @SuppressLint("MissingPermission")
    override fun subscribeTo(): Observable<Boolean> {
        connectivityCallback = ConnectivityCallback(o)
        cm.registerNetworkCallback(networkRequest!!, connectivityCallback!!)
        return o!!.startWithItem(getNetworkStatus()).hide()
    }

    override fun dispose() {
      cm.unregisterNetworkCallback(connectivityCallback!!)
      o = null
      connectivityCallback = null
    }
}