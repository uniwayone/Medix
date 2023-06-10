package app.slyworks.base_feature.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import app.slyworks.base_feature.R

/**
 *Created by Joshua Sylvanus, 5:26 AM, 12/10/2022.
 */
class CustomButton
@JvmOverloads
constructor(
    context: Context,
    attrs:AttributeSet? = null,
    defStyleAttr:Int = 0) : ConstraintLayout(context,attrs, defStyleAttr) {
    //region Vars
    private val btn:AppCompatButton
    private val progress:ProgressBar
    private val view: View
    //endregion

    init {
        view = inflate(context, R.layout.custom_button, this)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                   ViewGroup.LayoutParams.MATCH_PARENT)
        btn = view.findViewById(R.id.btn_custom_btn)
        progress = view.findViewById(R.id.progress_custom_btn)
    }

    fun setOnClickListener(func:() -> Unit):Unit = view.setOnClickListener{ func.invoke() }

    fun toggleLoading(status:Boolean){
        if(status){
            btn.text = ""
            progress.setVisibility(View.VISIBLE)
        }else{
            btn.text = "login"
            progress.setVisibility(View.GONE)
        }
    }
}