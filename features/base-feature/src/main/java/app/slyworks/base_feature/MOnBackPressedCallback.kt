package app.slyworks.base_feature

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import app.slyworks.base_feature.ui.ExitDialog

open class MOnBackPressedCallback(private val activity: Activity)
    : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
        if(!ActivityUtils.isLastActivity()){
            isEnabled = false
            activity.onBackPressed()
            return
        }

        ExitDialog.getInstance()
            .show((activity as AppCompatActivity).supportFragmentManager, "exit dialog")
    }
}