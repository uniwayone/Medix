package app.slyworks.room_lib.room_models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


/**
 * Created by Joshua Sylvanus, 2:32 PM, 9/1/2022.
 */

@Parcelize
@Entity
data class FBUserDetails(
    @ColumnInfo(name = "account_type") val accountType: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "fullname") val fullName:String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "sex") val sex:String,
    @ColumnInfo(name = "age") val age:String,
    @PrimaryKey
    @ColumnInfo(name = "firebase_uid") val firebaseUID: String,
    @ColumnInfo(name = "agora_uid") val agoraUID: String,
    @ColumnInfo(name = "fcm_registration_token") val FCMRegistrationToken:String,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "history") var history: MutableList<String>?,
    @ColumnInfo(name = "specialization") var specialization: MutableList<String>?)
    : Parcelable

