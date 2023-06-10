package app.slyworks.base_feature.ui

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.collection.SimpleArrayMap
import app.slyworks.base_feature.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.rxjava3.subjects.PublishSubject


/**
 *Created by Joshua Sylvanus, 4:01 PM, 11/05/2022.
 */
class PermissionsRationaleDialog(private var launcher: ActivityResultLauncher<String>?,
                                 private val permission:String,
                                 private val subject: PublishSubject<PermissionStatus>) : BaseDialogFragment(){
    //region Vars
    //private var mO:PublishSubject<Boolean> = PublishSubject.create()

    private lateinit var tvText:TextView
    private lateinit var tvAccept:TextView
    private lateinit var tvCancel:TextView
    //endregion

    override fun isCancelable(): Boolean  = false

    override fun onDestroy() {
        launcher = null
        super.onDestroy()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme).apply {
            val dialogView = onCreateView(LayoutInflater.from(requireContext()),null, savedInstanceState)
            dialogView?.let {
                onViewCreated(it,savedInstanceState)
            }
            setView(dialogView)
        }.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_permission_rationale2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       tvText = view.findViewById(R.id.tvText_permissions_rationale)
       tvAccept = view.findViewById(R.id.tvAccept_dialog_permissions_rationale)
       tvCancel = view.findViewById(R.id.tvCancel_dialog_permissions_rationale)

       tvText.text = PermissionManager.permissionToFriendlyMessageMap[permission]

       tvAccept.setOnClickListener {
           launcher?.launch(permission)
           this.dismiss()
       }

       tvCancel.setOnClickListener {
           subject.onNext(PermissionManager.constructPermissionStatus(CurrentStatus.DECLINED))
           this.dismiss()
       }
    }
}