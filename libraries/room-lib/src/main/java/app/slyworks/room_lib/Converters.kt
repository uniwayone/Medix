package app.slyworks.room_lib

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 *Created by Joshua Sylvanus, 4:52 AM, 28/08/2022.
 */
class Converters {
    @TypeConverter
    fun fromString(value:String):List<String>{
        val listType = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson<List<String>>(value, listType)
    }

    @TypeConverter
    fun fromList(value: List<String>): String {
        return Gson().toJson(value)
    }
}