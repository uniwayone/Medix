package app.slyworks.base_feature

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.SimpleArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.slyworks.base_feature.ui.PermissionsRationaleDialog
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.utils.onNextAndComplete
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.flow


/**
 * Created by Joshua Sylvanus, 2:48 PM, 12/16/2021.
 */
enum class CurrentStatus { ACCEPTED, DECLINED, PERMANENTLY_DECLINED }
data class PermissionStatus(val permission:String, val status:CurrentStatus, var text:String)

class PermissionManager {
    //region Vars
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private lateinit var permissionsLauncher:ActivityResultLauncher<String>
    private val subject:PublishSubject<Outcome> = PublishSubject.create()
    private val internalSubject:PublishSubject<PermissionStatus> = PublishSubject.create()
    val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    companion object{
        private var currentPermissionIndex:Int = -1
        private lateinit var permissions:List<String>

        @JvmStatic
        fun constructPermissionStatus(status:CurrentStatus):PermissionStatus{
            val p:String = permissions[currentPermissionIndex]
            return PermissionStatus(
                permission = p,
                status = status,
                text = permissionToFriendlyNameMap[p]!!)
        }

        private val permissionList:MutableList<String> = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        )

        @JvmStatic
        val permissionToFriendlyMessageMap: SimpleArrayMap<String, String> = SimpleArrayMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE , "Medix needs this permission to access files like pictures stored on the phone memory")
            put(Manifest.permission.READ_EXTERNAL_STORAGE , "Medix needs this permission to access files stored on the memory of your phone for proper functioning")
            put(Manifest.permission.CAMERA , "Medix needs this permission to access your phone's camera to enable capturing of images")
            put(Manifest.permission.ACCESS_BACKGROUND_LOCATION , "Medix needs this permission to be able to access location updates in the background")
            put(Manifest.permission.ACCESS_FINE_LOCATION , "Medix needs this permission access to your current location for some of its services to work")
            put(Manifest.permission.ACCESS_COARSE_LOCATION , "Medix needs this permission access to your current location for some of its services to work")
            put(Manifest.permission.RECORD_AUDIO , "Medix needs this permission to record audio to make voice calls and video calls possible")
            put(Manifest.permission.READ_PHONE_STATE , "Medix needs this permission to get cellular network details necessary for the proper functioning of the app")
        }

        @JvmStatic
        val permissionToFriendlyNameMap: SimpleArrayMap<String, String> = SimpleArrayMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE , "save files to device storage")
            put(Manifest.permission.READ_EXTERNAL_STORAGE , "read files from device storage")
            put(Manifest.permission.CAMERA , "use phone's camera to take pictures and video calls")
            put(Manifest.permission.ACCESS_BACKGROUND_LOCATION , "access your location to locate medical centers near you when the app is in the background")
            put(Manifest.permission.ACCESS_FINE_LOCATION , "access your location to locate medical centers near you with a high level of accuracy")
            put(Manifest.permission.ACCESS_COARSE_LOCATION , "access your location to locate medical centers near you")
            put(Manifest.permission.RECORD_AUDIO , "audio for voice and video calls")
            put(Manifest.permission.READ_PHONE_STATE , "access current phone operating status")
        }
    }

    @SuppressLint("NewApi")
    fun initialize(activity: Activity,
                   vararg perms:String){
        this.activity = activity
        permissions = perms.toList()

        permissionsLauncher =
            (activity as AppCompatActivity).registerForActivityResult(
                ActivityResultContracts.RequestPermission()) { result ->
                if(!result || result == null){
                    val permission:String = permissions[currentPermissionIndex]
                    if(activity.shouldShowRequestPermissionRationale(permission))
                        PermissionsRationaleDialog(permissionsLauncher, permission, internalSubject)
                            .show(activity.supportFragmentManager, "")
                    else
                        internalSubject.onNext(constructPermissionStatus(CurrentStatus.PERMANENTLY_DECLINED))
                }else
                    internalSubject.onNext(constructPermissionStatus(CurrentStatus.ACCEPTED))
            }
    }

    fun initialize(fragment:Fragment,
                   vararg perms: String){
        this.fragment = fragment
        permissions = perms.toList()

        permissionsLauncher =
            fragment.registerForActivityResult(
                ActivityResultContracts.RequestPermission()){ result ->
                if(!result || result == null){
                    val permission:String = permissions[currentPermissionIndex]
                    if(fragment.shouldShowRequestPermissionRationale(permission))
                        PermissionsRationaleDialog(permissionsLauncher, permission, internalSubject)
                            .show(fragment.parentFragmentManager, "")
                    else
                        internalSubject.onNext(constructPermissionStatus(CurrentStatus.PERMANENTLY_DECLINED))
                }else
                    internalSubject.onNext(constructPermissionStatus(CurrentStatus.ACCEPTED))
            }
    }

    private fun unbind(){
        disposables.clear()
    }
    fun getPermissionsObservable():Observable<Outcome> = subject.hide()

    private fun isPermissionGranted(permission:String):Boolean{
        val context: Context =
            if (activity != null)
                activity!!
            else
                fragment!!.requireContext()

        return (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun isPermissionDenied(permission:String):Boolean {
        val context: Context =
            if (activity != null)
                activity!!
            else
                fragment!!.requireContext()

        return (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED)
    }

    @SuppressLint("NewApi")
    fun requestPermissions(){
        currentPermissionIndex = -1

        val fragmentManager:FragmentManager =
            if(activity != null)
                (activity as AppCompatActivity).supportFragmentManager
            else
                fragment!!.parentFragmentManager

        val shouldShowPermissionRationale:(String) -> Boolean = {
            (if(activity != null)
                activity!!.shouldShowRequestPermissionRationale(it)
            else
                fragment!!.shouldShowRequestPermissionRationale(it))
        }

        val processPermission: (String) -> Unit = {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                permissionsLauncher.launch(it)
            else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowPermissionRationale(it)){
                    PermissionsRationaleDialog(permissionsLauncher, it, internalSubject)
                        .show(fragmentManager, "")
                }else {
                    permissionsLauncher.launch(it)
                }
            }
        }

        /* mutating outside state, not good */
       /* this poorly structured code is keeping a list of failed permissions */
        val result:MutableList<PermissionStatus> = mutableListOf()

        disposables +=
        internalSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: PermissionStatus ->
                    if(it.status != CurrentStatus.ACCEPTED)
                        result.add(it)

                    if(currentPermissionIndex == permissions.size-1){
                      var r:Outcome = Outcome.SUCCESS<Unit>()
                      if(result.isNotEmpty())
                          r = Outcome.FAILURE<List<PermissionStatus>>(value = result)

                       subject.onNext(r)
                       disposables.clear()
                       return@subscribe
                    }

                    processPermission(permissions[++currentPermissionIndex])
                }

        /* starting the process, after here it would be started from the subscription to internal subject */
        processPermission(permissions[++currentPermissionIndex])
    }

}



