package app.slyworks.base_feature.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.slyworks.base_feature.BaseBottomSheetDialogFragment
import app.slyworks.base_feature.R


/**
 * Created by Joshua Sylvanus, 11:13 PM, 1/4/2022.
 */
class TermsAndConditionsBSDialog() : BaseBottomSheetDialogFragment() {

    companion object{
        @JvmStatic
        fun getInstance(): TermsAndConditionsBSDialog {
           return TermsAndConditionsBSDialog()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.bottomsheet_terms_and_conditions, null)
        val ivCancel:ImageView = view.findViewById(R.id.ivCancelBSTC)
        ivCancel.setOnClickListener { dismiss() }
        return view
    }
}