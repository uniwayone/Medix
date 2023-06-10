package app.slyworks.navigation_feature

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import app.slyworks.navigation_feature.interfaces.ActivityContinuation
import app.slyworks.navigation_feature.interfaces.FragmentContinuation
import app.slyworks.navigation_feature.interfaces.FragmentContinuationStateful
import app.slyworks.navigation_feature.impl.ActivityContinuationImpl
import app.slyworks.navigation_feature.impl.FragmentContinuationImpl
import app.slyworks.navigation_feature.impl.FragmentContinuationStatefulImpl


/**
 * Created by Joshua Sylvanus, 9:27 PM, 20/08/2022.
 */

const val DEFAULT_INT_VALUE = -120934
const val DEFAULT_FLOAT_VALUE = -0.1020304050F
const val DEFAULT_DOUBLE_VALUE = -120934.012
const val DEFAULT_LONG_VALUE = 1020304050L

class Navigator private constructor() {

    init {
        throw UnsupportedOperationException("this class is not to be instantiated")
    }

    companion object{
        @JvmStatic
        fun minimizeApp(from:Context){
           val a = (from as AppCompatActivity)
           /*val intent = Intent(Intent.ACTION_MAIN)
           intent.addCategory(Intent.CATEGORY_HOME)
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
           a.startActivity(intent)*/

           a.moveTaskToBack(true)
        }

        @JvmStatic
        fun restartApp(from:Context){
            val a = (from as AppCompatActivity)
            a.startActivity(Intent(from.applicationContext, a::class.java))
            a.finish()
        }

        @JvmStatic
        inline fun <reified T: Activity> intentFor(from: Context): ActivityContinuation {
            return ActivityContinuationImpl(Intent(from, T::class.java), from as AppCompatActivity)
        }

        @JvmStatic
        fun intentFor(from: Context, clazz: Class<out AppCompatActivity>): ActivityContinuation {
            return ActivityContinuationImpl(Intent(from, clazz), from as AppCompatActivity)
        }

        @JvmStatic
        fun intentFor(from:Context, intentFilter:String): ActivityContinuation {
            return ActivityContinuationImpl(Intent(intentFilter).setPackage(from.packageName), from as AppCompatActivity)
        }
        @JvmStatic
        fun intentFromIntentFilter(from:Context, intentFilter:String): Intent =
            Intent(intentFilter).setPackage(from.packageName)

        @JvmStatic
        fun transactionFrom(fragmentManager: FragmentManager): FragmentContinuation {
            return FragmentContinuationImpl(fragmentManager)
        }

        @JvmStatic
        fun transactionWithStateFrom(fragmentManager: FragmentManager): FragmentContinuationStateful {
            return FragmentContinuationStatefulImpl(fragmentManager)
        }

        @JvmStatic
        inline fun <reified T> Intent.getExtra(key:String, defaultValue:T? = null):T?{
            return when(T::class){
                String::class ->{
                    val s: String = this.getStringExtra(key) ?: return defaultValue

                    s as? T
                }
                Int::class -> {
                    val i:Int = this.getIntExtra(key, DEFAULT_INT_VALUE)
                    if(i == DEFAULT_INT_VALUE)
                        return defaultValue

                    i as? T
                }
                Long::class -> {
                    val i:Long = this.getLongExtra(key, DEFAULT_LONG_VALUE)
                    if(i == DEFAULT_LONG_VALUE)
                        return defaultValue

                    i as? T
                }
                Double::class-> {
                    val d:Double = this.getDoubleExtra(key, DEFAULT_DOUBLE_VALUE)
                    if(d == DEFAULT_DOUBLE_VALUE)
                        return defaultValue

                    d as? T
                }
                Bundle::class->{
                    val bu:Bundle = this.getBundleExtra(key) ?: return defaultValue
                    bu as? T
                }
                ByteArray::class -> {
                    val ba:Byte = this.getByteExtra(key, Byte.MIN_VALUE)
                    if(ba == Byte.MIN_VALUE)
                        return defaultValue

                    ba as? T
                }
                else -> throw IllegalArgumentException("type of ${T::class} is not supported")
            }
        }

        @JvmStatic
        fun <T : Parcelable> Intent.getParcelable(key:String):T? {
            return this.getParcelableExtra<T>(key) as? T
        }
    }
}