package app.slyworks.network_lib.di

import android.content.Context
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.NetworkLibScope
import app.slyworks.network_lib.NetworkRegister
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 5:00 PM, 23/07/2022.
 */

@Module
object NetworkModule {

    @Provides
    @NetworkLibScope
    fun provideNetworkRegister(context: Context): NetworkRegister =
        NetworkRegister(context)
}