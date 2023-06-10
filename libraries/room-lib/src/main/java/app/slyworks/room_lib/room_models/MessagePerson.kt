package app.slyworks.room_lib.room_models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


/**
 *Created by Joshua Sylvanus, 8:29 PM, 1/19/2022.
 */

@Parcelize
@Entity
data class MessagePerson(
    @PrimaryKey
    @ColumnInfo(name = "firebase_uid") var firebaseUID:String): Parcelable