package app.slyworks.auth_lib

import app.slyworks.constants_lib.READ
import app.slyworks.data_lib.DataManager
import app.slyworks.data_lib.models.PersonVModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 *Created by Joshua Sylvanus, 9:17 PM, 27/06/2022.
 */
class PersonsManager(private val dataManager: DataManager) {

    fun updateLastMessageInfo(firebaseUID:String){
        CoroutineScope(Dispatchers.IO).launch {
            val person: PersonVModel =
               dataManager.getPersonByID(firebaseUID) ?: return@launch

               dataManager
                .updatePersons(person.apply {
                    lastMessageStatus = READ
                    unreadMessageCount = 0
                })
        }

    }

    fun updateLastMessageTimeStamp(firebaseUID: String, timeStamp:String) {
        CoroutineScope(Dispatchers.IO).launch{
           val person: PersonVModel =
             dataManager.getPersonByID(firebaseUID) ?: return@launch

             dataManager
                 .updatePersons(person.apply {
                     lastMessageTimeStamp = timeStamp
                 })
        }
    }
}