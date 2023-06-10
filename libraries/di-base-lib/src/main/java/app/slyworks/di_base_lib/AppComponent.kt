package app.slyworks.di_base_lib

import android.annotation.SuppressLint
import android.content.Context


/**
 * Created by Joshua Sylvanus, 6:59 AM, 25/11/2022.
 */
@SuppressLint("StaticFieldLeak")
object AppComponent {
    private lateinit var context: Context
    fun getContext(): Context = context
    fun setContext(ctx:Context){ context = ctx }
}