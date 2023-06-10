package app.slyworks.auth_feature.registration

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.auth_feature.R
import app.slyworks.base_feature.BaseDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * Created by Joshua Sylvanus, 9:29 PM, 12/11/2022.
 */
class PatientRegistrationIntroDialog : BaseDialogFragment() {

    override fun isCancelable(): Boolean  = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme).apply {
            val dialogView = onCreateView(LayoutInflater.from(requireContext()),null, savedInstanceState)
            dialogView?.let { onViewCreated(it, savedInstanceState) }
            setView(dialogView)
        }.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_patient_reg_intro, container, false)
    }
}