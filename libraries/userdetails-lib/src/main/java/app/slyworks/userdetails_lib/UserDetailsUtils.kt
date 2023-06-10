package app.slyworks.userdetails_lib

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import app.slyworks.room_lib.room_models.FBUserDetails
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


/**
 *Created by Joshua Sylvanus, 12:34 PM, 1/9/2022.
 */
class UserDetailsUtils(private val context: Context) {
     //region Vars
      var user: FBUserDetails? = null
      var user2: FBUserDetails? = null

       private val o:PublishSubject<FBUserDetails> = PublishSubject.create()

    private val Context.userDetailsProtoDataStore: DataStore<UserDetails> by
    dataStore(
        fileName = "user_details.pb",
        serializer = UserDataSerializer)

    //endregion

    init {
       observeUser()
    }

    /*for something like profile picture change,for it to happen immediately*/
    fun observeUserDetails(): Observable<FBUserDetails> = o.hide()

    /*for log out situations*/
    fun clearUserData(){
        user = null
        user2 = null
    }

    private fun observeUser(){
      CoroutineScope(Dispatchers.IO).launch {
      getUserFromDataStore()
                .distinctUntilChanged()
                .collectLatest {
                    user = it
                    user2 = it

                    o.onNext(user2!!)
            }
        }

    }

    fun getUserFromDataStore(): Flow<FBUserDetails> {
        return context.userDetailsProtoDataStore
            .data
            .map { userDetails ->
                var history: MutableList<String>? = null
                var specialization: MutableList<String>? = null
                if (userDetails.accountType == "PATIENT")
                    history = userDetails.historyList
                else
                    specialization = userDetails.specializationList

                FBUserDetails(
                    userDetails.accountType,
                    userDetails.firstName,
                    userDetails.lastName,
                    userDetails.fullName,
                    userDetails.email,
                    userDetails.sex,
                    userDetails.age,
                    userDetails.firebaseUID,
                    userDetails.agoraUID,
                    userDetails.fbRegistrationToken,
                    userDetails.imageUri,
                    history,
                    specialization)

            }
    }

    fun clearUserDetails() {
        CoroutineScope(Dispatchers.IO).launch{
            context.userDetailsProtoDataStore.updateData {
                user = null
                user2 = null

                it.toBuilder()
                  .clear()
                  .build()
            }
        }
    }

    suspend fun saveUserToDataStore(userDetails: FBUserDetails) {
        context.userDetailsProtoDataStore.updateData { details ->
            val _details: UserDetails.Builder = details.toBuilder()
                .clearHistory()
                .clearSpecialization()
                .setAccountType(userDetails.accountType)
                .setFirstName(userDetails.firstName)
                .setLastName(userDetails.lastName)
                .setFullName(userDetails.fullName)
                .setEmail(userDetails.email)
                .setSex(userDetails.sex)
                .setAge(userDetails.age)
                .setFirebaseUID(userDetails.firebaseUID)
                .setAgoraUID(userDetails.agoraUID)
                .setFBRegistrationToken(userDetails.FCMRegistrationToken)
                .setImageUri(userDetails.imageUri)

            if(userDetails.accountType == "PATIENT")
                _details.addAllHistory(userDetails.history)
            else
                _details.addAllSpecialization(userDetails.specialization)

            _details.build()
        }
    }
}