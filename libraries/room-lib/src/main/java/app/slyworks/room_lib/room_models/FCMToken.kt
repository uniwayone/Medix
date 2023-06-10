package app.slyworks.room_lib.room_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 *Created by Joshua Sylvanus, 9:58 AM, 13/05/2022.
 */
@Entity
data class FCMToken(
    @ColumnInfo(name = "token")
    @PrimaryKey
    var token:String = "") {
    constructor():this(
        token = "")
}