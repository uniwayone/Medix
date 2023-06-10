package app.slyworks.data_lib.di

import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.crypto_lib.di.CryptoComponent
import app.slyworks.crypto_lib.di.CryptoModule
import app.slyworks.data_lib.DataManager
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.DataLibScope
import app.slyworks.room_lib.daos.CallHistoryDao
import app.slyworks.room_lib.daos.ConsultationRequestDao
import app.slyworks.room_lib.daos.MessageDao
import app.slyworks.room_lib.daos.PersonDao
import app.slyworks.room_lib.di.RoomComponent
import app.slyworks.room_lib.di.RoomModule
import app.slyworks.userdetails_lib.UserDetailsUtils
import app.slyworks.userdetails_lib.di.UserDetailsComponent
import app.slyworks.userdetails_lib.di.UserDetailsModule
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 7:59 PM, 27/11/2022.
 */
@Module
object DataModule {
  /* @Provides
   @DataLibScope
   fun provideDataManager(messageDao:MessageDao,
                          personDao: PersonDao,
                          callHistoryDao: CallHistoryDao,
                          consultationRequestDao: ConsultationRequestDao,
                          userDetailsUtils: UserDetailsUtils,
                          cryptoHelper: CryptoHelper): DataManager =
       DataManager(
           messageDao,
           personDao,
           callHistoryDao,
           consultationRequestDao,
           userDetailsUtils,
           cryptoHelper)*/

 @Provides
   @DataLibScope
   fun provideDataManager(roomComponent: RoomComponent,
                          userDetailsComponent: UserDetailsComponent,
                          cryptoComponent: CryptoComponent): DataManager =
       DataManager(
           roomComponent.getMessageDao(),
           roomComponent.getPersonDao(),
           roomComponent.getCallHistoryDao(),
           roomComponent.getConsultationRequestDao(),
           userDetailsComponent.getUserDetailsUtils(),
           cryptoComponent.getCryptoHelper())
}