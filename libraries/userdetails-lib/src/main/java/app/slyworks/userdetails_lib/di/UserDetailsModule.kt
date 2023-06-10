package app.slyworks.userdetails_lib.di

import android.content.Context
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.UserDetailsLibScope
import app.slyworks.userdetails_lib.UserDetailsUtils
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 8:43 PM, 23/07/2022.
 */

@Module
object UserDetailsModule{
    @Provides
    @UserDetailsLibScope
    fun provideUserDetailsUtils(context:Context): UserDetailsUtils =
        UserDetailsUtils(context)
}