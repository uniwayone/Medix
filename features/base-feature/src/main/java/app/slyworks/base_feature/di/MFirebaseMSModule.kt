package app.slyworks.base_feature.di

import android.content.Context
import app.slyworks.auth_lib.LoginManager
import app.slyworks.auth_lib.UsersManager
import app.slyworks.auth_lib.di.AuthApplicationScopedComponent
import app.slyworks.base_feature.NotificationHelper
import app.slyworks.base_feature.WorkInitializer
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.di.DataComponent
import app.slyworks.di_base_lib.MFirebaseMSScope
import app.slyworks.network_lib.NetworkRegister
import app.slyworks.network_lib.di.NetworkComponent
import app.slyworks.utils_lib.PreferenceManager
import app.slyworks.utils_lib.di.UtilsComponent
import dagger.Module
import dagger.Provides

@Module
object MFirebaseMSModule {
  @Provides
  @MFirebaseMSScope
  fun providePreferenceManager(component:UtilsComponent):PreferenceManager
  = component.getPreferenceManager()

  @Provides
  @MFirebaseMSScope
  fun provideLoginManager(component:AuthApplicationScopedComponent):LoginManager
  = component.getLoginManager()

  @Provides
  @MFirebaseMSScope
  fun provideUsersManager(component: AuthApplicationScopedComponent):UsersManager
  = component.getUsersManager()

  @Provides
  @MFirebaseMSScope
  fun provideNotificationHelper(context: Context):NotificationHelper
  = NotificationHelper(context)

  @Provides
  @MFirebaseMSScope
  fun provideDataManager(component:DataComponent):DataManager
  = component.getDataManager()

  @Provides
  @MFirebaseMSScope
  fun provideNetworkRegister(component:NetworkComponent):NetworkRegister
  = component.getNetworkRegister()

  @Provides
  @MFirebaseMSScope
  fun provideWorkInitializer(context: Context):WorkInitializer
  = WorkInitializer(context)
}
