package app.slyworks.utils_lib.di

import android.content.Context
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.UtilsLibScope
import app.slyworks.utils_lib.PreferenceManager
import app.slyworks.utils_lib.TaskManager
import app.slyworks.utils_lib.TimeHelper
import dagger.Module
import dagger.Provides


/**
 *Created by Joshua Sylvanus, 9:54 PM, 23/07/2022.
 */

@Module
object UtilsModule {
    @Provides
    @UtilsLibScope
    fun providePreferenceManager(context:Context): PreferenceManager =
        PreferenceManager(context)

    @Provides
    @UtilsLibScope
    fun provideTimeHelper(): TimeHelper = TimeHelper()

    @Provides
    @UtilsLibScope
    fun provideTaskManager(): TaskManager = TaskManager
}