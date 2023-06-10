package app.slyworks.userdetails_lib.di

import android.content.Context
import app.slyworks.di_base_lib.AppComponent
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.UserDetailsLibScope
import app.slyworks.userdetails_lib.UserDetailsUtils
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 7:55 PM,  7:55 PM, 02 - Dec - 2022.
 */
@Component(modules = [UserDetailsModule::class])
@UserDetailsLibScope
interface UserDetailsComponent {
    companion object{
      private var instance:UserDetailsComponent? = null

      @JvmStatic
      fun getInstance():UserDetailsComponent{
          if(instance == null)
              instance =
              DaggerUserDetailsComponent.builder()
                  .setContext(AppComponent.getContext())
                  .build()

          return instance!!
      }
    }

    fun getUserDetailsUtils(): UserDetailsUtils

    @Component.Builder
    interface Builder{
        fun setContext(@BindsInstance ctx:Context):Builder
        fun build():UserDetailsComponent
    }
}
