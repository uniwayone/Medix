package app.slyworks.network_lib

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 *Created by Joshua Sylvanus, 4:19 PM, 29/05/2022.
 */
internal class ConnectivityCallback(private var o: PublishSubject<Boolean>?)
    : ConnectivityManager.NetworkCallback(){

    override fun onLost(network: Network) {
        super.onLost(network)
        o!!.onNext(false)
    }

    override fun onAvailable(network: Network) {
       o!!.onNext(true)
    }

    override fun onUnavailable() {
       o!!.onNext(false)
    }

}