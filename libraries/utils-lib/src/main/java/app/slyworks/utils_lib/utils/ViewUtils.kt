package app.slyworks.utils_lib.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 *Created by Joshua Sylvanus, 8:10 AM, 26/04/2022.
 */

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * show a Snackbar with duration set to #Snackbar.LENGTH_LONG */
fun Context.showToast(message:String):Unit =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun showMessage(message:String, view: View){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
}

fun View.setChildViewsStatus(status:Boolean){
    isEnabled = status

    if(this is ViewGroup){
        val viewGroup: ViewGroup? = this as? ViewGroup

        for(i in 0 until getChildCount()){
            //recursively searching the child view, if its a parent too
            val child:View = getChildAt(i)
            child.setChildViewsStatus(status)
        }
    }
}

fun ImageView.displayImage(imageID: Int) {
    Glide.with(this.context)
        .load(imageID)
        .dontTransform()
        .into(this);
}

fun ImageView.displayImage(imageID: String) {
    Glide.with(this.context)
        .load(imageID)
        .centerCrop()
        .into(this);
}

fun ImageView.displayImage(imageID: Uri) {
    Glide.with(this.context)
        .load(imageID)
        .into(this);
}

fun ImageView.displayGif(imageID: Int) {
    Glide.with(this.context)
        .asGif()
        .load(imageID)
        .dontTransform()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this);
}

fun ImageView.displayGif(imageID: String) {
    Glide.with(this.context)
        .asGif()
        .load(imageID)
        .dontTransform()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this);
}

fun Activity.setStatusBarVisibility(status: Boolean){
    /* remember this is cleared when user navigates away from the activity */
   if(status)
       window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
   else {
       window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
       findViewById<ViewGroup>(android.R.id.content).getChildAt(0).setFitsSystemWindows(true) /*.getchildAt(0)*/
   }
}

fun Activity.closeKeyboard(){
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

fun Activity.closeKeyboard2(){
    //to show soft keyboard
    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
}

fun Activity.closeKeyboard3(){
    val inputManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(currentFocus?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Activity.closeKeyboard5(onClose:(() -> Unit)? = null){
    val inputManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(currentFocus?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)

    onClose?.invoke()
}

fun Activity.closeKeyboard4(rootView:View){
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(rootView.windowToken, 0)
}

fun displayMessage(message:String, view:View):Unit =
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
