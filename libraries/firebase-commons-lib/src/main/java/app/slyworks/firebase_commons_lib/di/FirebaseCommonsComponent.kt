package app.slyworks.firebase_commons_lib.di

import app.slyworks.di_base_lib.ApplicationScope
import app.slyworks.di_base_lib.FirebaseCommonsLibScope
import app.slyworks.firebase_commons_lib.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import dagger.Component


/**
 * Created by Joshua Sylvanus, 7:57 PM,  7:57 PM, 02 - Dec - 2022.
 */
@Component(modules = [FirebaseCommonsModule::class])
@FirebaseCommonsLibScope
interface FirebaseCommonsComponent {
    companion object{
      private var instance:FirebaseCommonsComponent? = null

      @JvmStatic
      fun getInstance():FirebaseCommonsComponent{
          if(instance == null)
              instance =
              DaggerFirebaseCommonsComponent.create()

          return instance!!
      }
    }

    fun getFirebaseAuth():FirebaseAuth
    fun getFirebaseStorage():FirebaseStorage
    fun getFirebaseDatabase():FirebaseDatabase
    fun getFirebaseMessaging():FirebaseMessaging
    fun getFirebaseFirestore(): FirebaseFirestore
    fun getFirebaseUtils(): FirebaseUtils
}
