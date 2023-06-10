package app.slyworks.room_lib.di

import android.content.Context
import app.slyworks.di_base_lib.AppComponent
import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.RoomLibScope
import app.slyworks.room_lib.daos.CallHistoryDao
import app.slyworks.room_lib.daos.ConsultationRequestDao
import app.slyworks.room_lib.daos.MessageDao
import app.slyworks.room_lib.daos.PersonDao
import dagger.BindsInstance
import dagger.Component


/**
 * Created by Joshua Sylvanus, 7:48 PM,  7:48 PM, 02-December-2022.
 */
@Component(modules = [RoomModule::class])
@RoomLibScope
interface RoomComponent {
    companion object {
        private var instance:RoomComponent? = null

        @JvmStatic
        fun getInstance():RoomComponent{
            if(instance == null)
                instance = DaggerRoomComponent.builder()
                    .setContext(AppComponent.getContext())
                    .build()

            return instance!!
        }
    }

    fun getPersonDao():PersonDao
    fun getMessageDao(): MessageDao
    fun getCallHistoryDao(): CallHistoryDao
    fun getConsultationRequestDao(): ConsultationRequestDao

    @Component.Builder
    interface Builder{
        fun setContext(@BindsInstance ctx:Context):Builder
        fun build():RoomComponent
    }
}
