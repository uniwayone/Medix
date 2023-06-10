package app.slyworks.utils_lib.di

import android.content.Context
import app.slyworks.di_base_lib.AppComponent
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.UtilsLibScope
import app.slyworks.utils_lib.PreferenceManager
import app.slyworks.utils_lib.TaskManager
import app.slyworks.utils_lib.TimeHelper
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 8:08 AM, 02/12/2022.
 */
@Component(modules = [UtilsModule::class])
@UtilsLibScope
interface UtilsComponent {
    companion object{
        private var instance:UtilsComponent? = null

        @JvmStatic
        fun getInstance():UtilsComponent{
            if(instance == null)
               instance =
               DaggerUtilsComponent.builder()
                   .setContext(AppComponent.getContext())
                   .build()

            return instance!!
        }
    }

    fun getTimeHelper(): TimeHelper
    fun getTaskManager(): TaskManager
    fun getPreferenceManager(): PreferenceManager

    @Component.Builder
    interface Builder{
        fun setContext(@BindsInstance ctx:Context):Builder
        fun build():UtilsComponent
    }
}
