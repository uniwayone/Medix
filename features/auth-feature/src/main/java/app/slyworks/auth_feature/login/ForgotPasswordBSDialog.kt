package app.slyworks.auth_feature.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.auth_feature.R
import app.slyworks.base_feature.BaseBottomSheetDialogFragment


/**
 * Created by Joshua Sylvanus, 7:11 PM, 1/4/2022.
 */

class ForgotPasswordBSDialog(private val initFunction:((view:View?)->Unit)? = null)
    : BaseBottomSheetDialogFragment() {

    companion object {
        @JvmStatic
        fun getInstance(initFunction: ((view: View?) -> Unit)? = null): ForgotPasswordBSDialog {
            return ForgotPasswordBSDialog(initFunction)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_forgot_password, container, false)
        initFunction?.invoke(view)
        return view
    }

}