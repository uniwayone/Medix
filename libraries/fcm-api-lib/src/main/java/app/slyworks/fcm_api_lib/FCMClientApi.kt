package app.slyworks.fcm_api_lib

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface FCMClientApi {
    /*TODO:make BuildConfig.SERVER_KEY available for RELEASE as well*/
    @POST("send")
    @Headers("Content-type:application/json")
    fun sendCloudMessage(@Body
                         message: FirebaseCloudMessage,
                         @Header("Authorization")
                         key:String = ""/*"key=${BuildConfig.SERVER_KEY}"*/): Call<ResponseBody>
}