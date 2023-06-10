package app.slyworks.utils_lib

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.concurrent.Callable


/**
 *Created by Joshua Sylvanus, 7:22 PM, 1/3/2022.
 */
class CompressImageCallable(val uri: Uri) : Callable<ByteArray> {
    companion object{
        //region Vars
        private val TAG: String? = CompressImageCallable::class.simpleName
        private val MB_THRESHHOLD = 5.0 //max mb for picture to be uploaded
        private val MB = 1_000_000.0
        //endregion

        private fun getByteArray(bitmap:Bitmap):ByteArray{
            var imageByteArray:ByteArray? = null
            for(index in 1..10){
              val imageQuality:Int = 100/index
              val bytes:ByteArray = _getBytesFromBitmap(bitmap,imageQuality)

                Timber.e("CompressImageCallable.call(): megabytes: (" + (11 - index) + "0%) " + bytes.size / MB + " MB")

                val condition = (bytes.size / MB) < MB_THRESHHOLD
                if(condition){
                    imageByteArray = bytes
                    break
                }
            }

            return imageByteArray!!

        }
        private fun _getBytesFromBitmap(bitmap: Bitmap, quality:Int):ByteArray{
            val stream:ByteArrayOutputStream = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream)
            return stream.toByteArray()
        }
        private fun getBitmapFromFile(uri: Uri):Bitmap{
            var bitmap:Bitmap? = null
            try{
                if(Build.VERSION.SDK_INT < 28)
                    bitmap = MediaStore.Images.Media.getBitmap(ContentResolverStore.getContentResolver(), uri)
                else
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(ContentResolverStore.getContentResolver(), uri))
            }catch (e:Exception){
                Timber.e("call: CompressImageCallable.call() failed", e )
            }

            return bitmap!!
        }
    }
    override fun call(): ByteArray {
       val bitmap:Bitmap = getBitmapFromFile(uri)
        val imageAsByteArray:ByteArray = getByteArray(bitmap)
        return imageAsByteArray
    }


}