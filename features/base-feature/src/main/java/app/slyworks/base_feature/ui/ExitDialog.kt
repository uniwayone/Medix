package app.slyworks.base_feature.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import app.slyworks.base_feature.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess


/**
 *Created by Joshua Sylvanus, 9:11 PM, 2/7/2022.
 */
class ExitDialog : DialogFragment() {
    //region Vars
    private lateinit var tvCancel: TextView
    private lateinit var tvExit:TextView
    //endregion

    companion object{
        @JvmStatic
        fun getInstance(): ExitDialog = ExitDialog()
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
        val view = inflater.inflate(R.layout.dialog_exit2, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view:View){
        tvCancel = view.findViewById(R.id.tvCancel_dialog_exit)
        tvExit = view.findViewById(R.id.tvExit_dialog_exit)

        tvCancel.setOnClickListener { dismiss() }
        tvExit.setOnClickListener { exitProcess(0) }
    }
}