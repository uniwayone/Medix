package app.slyworks.fcm_api_lib

import app.slyworks.models_commons_lib.models.Data
import com.google.gson.annotations.SerializedName

data class FirebaseCloudMessage(
    @SerializedName("to")
    var to:String,
    @SerializedName("data")
    var data: Data
)