package app.slyworks.voice_call_feature

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import app.slyworks.base_feature.BaseDialogFragment
import app.slyworks.utils_lib.utils.onNextAndComplete
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.rxjava3.subjects.PublishSubject


/**
 * Created by Joshua Sylvanus, 3:12 PM, 17/05/2022.
 */
class SwitchToVideoCallDialog(private var o:PublishSubject<Boolean>?) : BaseDialogFragment() {
    //region Vars
    private lateinit var btnSwitch:Button
    private lateinit var btnCancel:Button
    private lateinit var tvDetails:TextView
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        o = null
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
        val view = inflater.inflate(R.layout.dialog_switch_to_video_call, container, false)
        init(view)
        return view
    }

    private fun init(view:View){
        tvDetails = view.findViewById(R.id.tvDetails_dialog_switch_to_video_call)
        btnSwitch = view.findViewById(R.id.btnSwitch_dialog_switch_to_video_call)
        btnCancel = view.findViewById(R.id.btnCancel_dialog_switch_to_video_call)

        btnSwitch.setOnClickListener {
            o!!.onNextAndComplete(true)
        }
        btnCancel.setOnClickListener {
            o!!.onNextAndComplete(false)
            dismiss()
        }
    }
}