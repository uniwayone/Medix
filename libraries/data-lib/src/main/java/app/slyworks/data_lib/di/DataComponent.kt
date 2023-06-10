package app.slyworks.data_lib.di

import android.app.Person
import android.content.Context
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.crypto_lib.di.CryptoComponent
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
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 8:52 PM, 02-Dec-2022.
 */
@Component(modules = [DataModule::class])
@DataLibScope
interface DataComponent {
    companion object{
        private var instance:DataComponent? = null

        @JvmStatic
        fun getInstance():DataComponent{
            if(instance == null)
                instance =
                DaggerDataComponent.builder()
                    .setCryptoComponent(
                        CryptoComponent.getInstance())
                    .setRoomComponent(
                        RoomComponent.getInstance())
                    .setUserDetailsComponent(
                        UserDetailsComponent.getInstance())
                    .build()

            return instance!!
        }
    }


    fun getDataManager():DataManager

    @Component.Builder
    interface Builder{
        fun setCryptoComponent(@BindsInstance component: CryptoComponent):Builder
        fun setUserDetailsComponent(@BindsInstance component: UserDetailsComponent):Builder
        fun setRoomComponent(@BindsInstance component: RoomComponent):Builder
        fun build():DataComponent
    }
}
