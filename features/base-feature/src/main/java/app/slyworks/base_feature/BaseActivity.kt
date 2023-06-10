package app.slyworks.base_feature

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.slyworks.base_feature.custom_views.NetworkStatusView
import app.slyworks.base_feature.di.BaseFeatureComponent
import app.slyworks.base_feature.di.DaggerBaseFeatureComponent
import app.slyworks.constants_lib.GOOGLE_API_SERVICES_ERROR_DIALOG_REQUEST_CODE
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.exitProcess


/**
 *Created by Joshua Sylvanus, 6:26 PM, 1/13/2022.
 */

open class BaseActivity : AppCompatActivity(), IValidForListening {
    //region Vars
    private var listenerManager: ListenerManager? = null
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        listenerManager = null
        ActivityUtils.decrementActivityCount()
    }

    override fun isValid(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        if(isValid())
            initDI()

        super.onCreate(savedInstanceState)

        initNetworkStatusView()

        ActivityUtils.incrementActivityCount()
    }

    private fun initDI(){
      if(listenerManager == null)
        listenerManager =
            BaseFeatureComponent.getInstance()
            .getListenerManager()
    }

    private fun initNetworkStatusView(){ }

    override fun onStart() {
        super.onStart()

        ActivityUtils.setForegroundStatus(true, this@BaseActivity::class.simpleName!!)

        if(isValid())
          listenerManager!!.start()
    }

    override fun onStop() {
        super.onStop()

        ActivityUtils.setForegroundStatus(false, this@BaseActivity::class.simpleName!!)

        if(isValid())
          listenerManager!!.stop()
    }

    override fun onResume() {
        super.onResume()
        /*to avoid doing too much work on the main thread */
        CoroutineScope(Dispatchers.Main).launch {
          handleGooglePlayServicesAvailability()
        }
    }

    private fun handleGooglePlayServicesAvailability(){
        if(checkGooglePlayServices())
            return

        Toast.makeText(this, "Error with google services",Toast.LENGTH_LONG).show()
        exitProcess(0) //equivalent of System.exit(0)
    }

    private fun checkGooglePlayServices():Boolean{
        val googlePlayInstance = GoogleApiAvailability.getInstance()

        val areServicesAvailable:Int = googlePlayInstance.isGooglePlayServicesAvailable(this)

        if(areServicesAvailable == ConnectionResult.SUCCESS){
            return true
        }else if(googlePlayInstance.isUserResolvableError(areServicesAvailable)){
            val dialog: Dialog? = googlePlayInstance.getErrorDialog(this, areServicesAvailable, GOOGLE_API_SERVICES_ERROR_DIALOG_REQUEST_CODE)
            dialog?.show()
            return true
        }else{
            return false
        }
    }



}