package app.slyworks.utils_lib

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * Created by Joshua Sylvanus, 8:01 PM, 18/05/2022.
 */
const val KEY_PREFS_DEFAULT = "app.slyworks.utils_lib.KEY_PREFS_DEFAULT"
const val DEFAULT_INT_VALUE = -1020304050
const val DEFAULT_FLOAT_VALUE = -0.1020304050F
const val DEFAULT_LONG_VALUE = 1020304050L

//val Context.dataStore: DataStore<Preferences> by PreferenceDataStore(name = "settings")

class PreferenceManager(private val context: Context) {
    //region Vars
     val prefs:SharedPreferences by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        context.getSharedPreferences(KEY_PREFS_DEFAULT, Context.MODE_PRIVATE)
        //PreferenceManager.getDefaultSharedPreferences(context)
     }
    private val editor:SharedPreferences.Editor = prefs.edit()
    //endregion

    fun clearPreference(vararg keys:String):Unit =
        keys.toList().forEach { editor.remove(it).apply() }


   /*inline fun <reified E>setWithDataStore(key: String, value:Any){

    }

    suspend fun incrementCounter() {
        context.dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }

    val exampleCounterFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[EXAMPLE_COUNTER] ?: 0
        }

    inline fun <reified E> getFromDataStore(key:String, default:E? = null):E?{
        when(E::class.simpleName){
            Int::class.simpleName ->{
                val KEY = intPreferenceKey(key)
                context.dataStore.data
                    .map{ preferences ->
                        preferences[KEY] ?: default
                    }
            }
        }
        val flow: Flow<E> =
           context.dataStore.data
               .map { preferences ->

               }
    }*/

    fun set(key:String, value:Any){
        CoroutineScope(Dispatchers.IO).launch{
            when(value){
                is Boolean -> editor.putBoolean(key, value).apply()
                is String -> editor.putString(key, value).apply()
                is Int -> editor.putInt(key, value).apply()
                is Float -> editor.putFloat(key, value).apply()
                is Long -> editor.putLong(key, value).apply()
                else -> throw IllegalArgumentException("type is not supported")
            }
        }
    }

    inline fun <reified T> get(key:String, defaultValue:T? = null):T?{
        var t:T? = null
        when(T::class){
            Boolean::class -> t = prefs.getBoolean(key, defaultValue as Boolean) as T
            String::class -> t = prefs.getString(key, defaultValue as? String) as? T?
            Int::class -> {
                val _t:Int = prefs.getInt(key, defaultValue as? Int ?: DEFAULT_INT_VALUE)
                if(_t != DEFAULT_INT_VALUE)
                   t = _t as T
            }
            Float::class ->{
                val _t:Float = prefs.getFloat(key, defaultValue as? Float ?: DEFAULT_FLOAT_VALUE)
                if(_t != DEFAULT_FLOAT_VALUE)
                    t = _t as T
            }
            Long::class ->{
                val _t:Long = prefs.getLong(key, defaultValue as? Long ?: DEFAULT_LONG_VALUE)
                if(_t != DEFAULT_LONG_VALUE)
                    t = _t as T
            }
            else -> throw UnsupportedOperationException()
        }

        return t
    }


}