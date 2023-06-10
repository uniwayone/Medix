package app.slyworks.communication_lib

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.FirebaseDatabase
import com.nhaarman.mockitokotlin2.mock
import com.slyworks.constants.NOT_SENT
import com.slyworks.models.room_models.Message
import com.slyworks.room.AppDatabase
import com.slyworks.room.daos.MessageDao
import com.slyworks.room.daos.PersonDao
import com.slyworks.userdetails.UserDetailsUtils
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

/**
 * Created by Joshua Sylvanus, 7:37 PM, 02/11/2022.
 */
@RunWith(RobolectricTestRunner::class)
class MessageManagerTest {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var messageDao: MessageDao
    private lateinit var personDao: PersonDao
    private lateinit var userDetailsUtils: UserDetailsUtils
    private lateinit var messageManager: app.slyworks.communication_lib.MessageManager2

   /* @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()*/

    @Rule
    val rxImmediateSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    val coroutineRule = CoroutineMainDispatcherRule()

    @BeforeClass
    fun setUp() {
        firebaseDatabase = mock()
        userDetailsUtils = mock()

        val appDatabase:AppDatabase =
            Room.inMemoryDatabaseBuilder(
               InstrumentationRegistry.getInstrumentation().context,
               AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        messageDao = Mockito.spy(appDatabase.getMessageDao())
        personDao = Mockito.spy(appDatabase.getPersonDao())

        messageManager =
            app.slyworks.communication_lib.MessageManager2(
                firebaseDatabase,
                messageDao,
                personDao,
                userDetailsUtils
            )
    }

    @Test
    fun `does addMessagesToDB() add messages to DB`() =
    runTest {
      val message: Message =
      Message(
          "",
          "",
      "",
          "",
          "",
          "",
          "",
          "1",
          NOT_SENT,
          "",
      "",
      "",
      "")

      messageManager.addMessagesToDB(message)

      assertEquals(null, messageDao.getMessageByID("1"))
    }

    @Test
    fun isJunit_working():Unit = assertEquals(1,1)
}