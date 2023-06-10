package app.slyworks.room_lib

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import app.slyworks.room_lib.daos.*
import app.slyworks.room_lib.room_models.*

/**
 * Created by Joshua Sylvanus, 3:23 PM, 1/9/2022.
 */
@Database(
    entities = [
       MessagePerson::class,
       Person::class,
       Message::class,
       CallHistory::class,
       ConsultationRequest::class
    ],
    version = 1,
    exportSchema = true )
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    companion object{
        //region Vars
        @JvmStatic
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        private val roomCallback: RoomDatabase.Callback = object : RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase):Unit = super.onCreate(db)
            override fun onOpen(db: SupportSQLiteDatabase):Unit = super.onOpen(db)
            override fun onDestructiveMigration(db: SupportSQLiteDatabase):Unit = super.onDestructiveMigration(db)
        }
        //endregion

        @Synchronized
        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "AppDatabase")
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build()
                }

                return INSTANCE!!
        }
    }

    abstract fun getMessageDao(): MessageDao
    abstract fun getMessagePersonDao(): MessagePersonDao
    abstract fun getPersonDao(): PersonDao
    abstract fun getCallHistoryDao(): CallHistoryDao
    abstract fun getConsultationRequestDao(): ConsultationRequestDao
}