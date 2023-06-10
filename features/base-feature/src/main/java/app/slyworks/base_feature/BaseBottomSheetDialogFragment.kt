package app.slyworks.base_feature

import android.content.DialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 *Created by Joshua Sylvanus, 5:04 PM, 12/05/2022.
 */
open class BaseBottomSheetDialogFragment(): BottomSheetDialogFragment(){
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}