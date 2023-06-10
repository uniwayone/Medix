package app.slyworks.utils_lib

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyInt

/**
 * Created by Joshua Sylvanus, 7:21 AM, 23/10/2022.
 */

class PreferenceManagerTest{
    private val key:String = "key"
    private var intValue:Int = -2
    private var tempIntValue:Int = 0
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var preferenceManager: PreferenceManager

    @Before
    fun setup(){
        val context:Context = mock()
        sharedPreferences = mock()
        editor = mock()

        whenever(context.getSharedPreferences(KEY_PREFS_DEFAULT, Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit())
            .thenReturn(editor)
        whenever(editor.putInt(eq(key), any()))
            .thenReturn(editor)
        whenever(editor.apply())
            .then { return@then it }

        preferenceManager = PreferenceManager(context)
    }

    @Test
    fun intValueSet_isValueRetrieved(){
        tempIntValue = 10

        whenever(editor.apply())
            .then{ return@then it}
            .apply { intValue = tempIntValue }
        whenever(sharedPreferences.getInt(eq(key), anyOrNull()))
            .thenReturn(intValue)

        preferenceManager.set(key, tempIntValue)

        assertEquals(10, preferenceManager.get<Int>(key))
    }

    @Test
    fun ifIntValue_doesNotExist_shouldReturnNull(){
       whenever(sharedPreferences.getInt(eq(key), anyOrNull()))
           .thenReturn(DEFAULT_INT_VALUE)

       assertEquals(null, preferenceManager.get<Int>(key))
    }

    @Test
    fun ifIntValue_doesNotExist_shouldReturnPassedDefaultValue(){
        whenever(sharedPreferences.getInt(eq(key), anyInt()))
            .doAnswer {
                return@doAnswer it.getArgument(1)
            }

        assertEquals(1, preferenceManager.get<Int>(key, 1))
    }

    @Test
    fun ifStringValue_doesNotExist_shouldReturnNull(){
        whenever(sharedPreferences.getString(eq(key), anyOrNull()))
            .thenReturn(null)

        assertEquals(null, preferenceManager.get<String>(key))
    }

    @Test
    fun ifStringValue_doesNotExist_shouldReturnPassedDefaultValue(){
       whenever(sharedPreferences.getString(eq(key), anyOrNull()))
           .doAnswer {
               return@doAnswer it.getArgument(1)
           }

       assertEquals("correct", preferenceManager.get<String>(key, "correct"))
    }

    @Test
    fun isJUnit_working():Unit = assertEquals(1,1)

    /*
    * inOrder(preferenceManager){
            verify(preferenceManager).set(eq(key), eq(10))
            verify(preferenceManager).get<Int>(eq(key))
        }
    *
    * */
}