package app.slyworks.auth_lib

import app.slyworks.constants_lib.KEY_LOGGED_IN_STATUS
import app.slyworks.utils_lib.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 *Created by Joshua Sylvanus, 12:11 PM, 12/10/2021.
 */
class MAuthStateListener(private val preferenceManager: PreferenceManager): FirebaseAuth.AuthStateListener {
    var loggedInStatus:Boolean = false
    var currentUser: FirebaseUser? = null


    override fun onAuthStateChanged(p0: FirebaseAuth) {
        currentUser = p0.currentUser
        loggedInStatus = p0.currentUser != null
        preferenceManager.set(KEY_LOGGED_IN_STATUS, loggedInStatus)
    }
}