package app.slyworks.utils_lib

import android.content.ContentResolver


/**
 * Created by Joshua Sylvanus, 9:26 PM, 12/07/2022.
 */
object ContentResolverStore {
    private var contentResolver: ContentResolver? = null

    fun getContentResolver(): ContentResolver = contentResolver!!
    fun setContentResolver(cr: ContentResolver){ contentResolver = cr }
    fun nullifyContentResolver(){ contentResolver = null }
}