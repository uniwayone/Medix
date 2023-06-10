package app.slyworks.firebase_commons_lib.di

import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.FirebaseCommonsLibScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import app.slyworks.firebase_commons_lib.FirebaseUtils
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 10:18 PM, 23/07/2022.
 */

@Module
object FirebaseCommonsModule {
    @Provides
    @FirebaseCommonsLibScope
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @FirebaseCommonsLibScope
    fun provideFirebaseStorage(): FirebaseStorage =
        FirebaseStorage.getInstance()

    @Provides
    @FirebaseCommonsLibScope
    fun provideFirebaseDatabase() : FirebaseDatabase =
        FirebaseDatabase.getInstance()

    @Provides
    @FirebaseCommonsLibScope
    fun provideFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()

    @Provides
    @FirebaseCommonsLibScope
    fun provideFirebaseFirestore():FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @FirebaseCommonsLibScope
    fun provideFirebaseUtils(firebaseDatabase:FirebaseDatabase,
                             firebaseStorage:FirebaseStorage,
                             firebaseFirestore:FirebaseFirestore): FirebaseUtils =
        FirebaseUtils(firebaseDatabase, firebaseStorage, firebaseFirestore)
}