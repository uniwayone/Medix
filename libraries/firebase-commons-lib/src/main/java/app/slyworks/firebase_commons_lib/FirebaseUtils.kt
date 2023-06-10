package app.slyworks.firebase_commons_lib

import app.slyworks.constants_lib.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


/**
 * Created by Joshua Sylvanus, 2:06 PM, 03/06/2022.
 */

class FirebaseUtils(private val firebaseDatabase: FirebaseDatabase,
                    private val firebaseStorage: FirebaseStorage,
                    private val firebaseFirestore: FirebaseFirestore) {

    fun getIsUserTypingRef(myUID:String,userUID:String):DatabaseReference =
        firebaseDatabase
            .reference
            .child("typing")
            .child(userUID)
            .child(myUID)

    fun getEncryptionDetailsRefFS():DocumentReference =
        firebaseFirestore
            .collection("encryption_details")
            .document("details")

    fun getEncryptionDetailsRef(): DatabaseReference =
        firebaseDatabase
            .reference
            .child("encryption_details")

    fun getUserDataRefForWorkManager(params: String): DatabaseReference {
        return firebaseDatabase
            .reference
            .child("users")
            .child(params)
            .child("details")
            .child("fcm_registration_token")
    }

    fun getUserDataRef(params: String): DatabaseReference {
        return firebaseDatabase
            .reference
            .child("users")
            .child(params)
            .child("details")
            .child("fcm_registration_token")
    }


    fun getVoiceCallRequestsRef(param: String): Query {
        return firebaseDatabase
            .reference
            .child("voice_call_requests")
            .child(param)
            .orderByChild("from")
            .equalTo(REQUEST_PENDING)
    }

    fun getVideoCallRequestsRef(param: String): Query {
        return firebaseDatabase
            .reference
            .child("video_call_requests")
            .child(param)
            .orderByChild("from")
            .equalTo(REQUEST_PENDING)
    }

    fun getFCMRegistrationTokenRefPath(params: String): String {
        return "/users/$params/details/FCMRegistrationToken"
    }

    fun getUserMessagesRef(params: String): Query {
        return firebaseDatabase
            .reference
            .child("messages")
            .child(params)
            .orderByChild("type")
            .equalTo(INCOMING_MESSAGE)
            .orderByChild("status")
            .equalTo(DELIVERED)
    }

    fun getFCMRegistrationTokenRef(params: String): DatabaseReference {
        return firebaseDatabase
            .reference
            .child("users")
            .child(params)
            .child("details")
            .child("FCMRegistrationToken")
    }

    fun getAllDoctorsRef(): Query {
        return firebaseDatabase
            .reference
            .child("users")
            .orderByChild("details/accountType")
            .equalTo("DOCTOR")

    }

    fun getUserDataForUIDRef(params: String): DatabaseReference {
        return firebaseDatabase
            .reference
            .child("users")
            .child(params)
            .child("details")
    }

    fun getUserVerificationStatusRef(UID:String):DatabaseReference =
        firebaseDatabase
            .reference
            .child("users")
            .child(UID)
            .child("is_verified")

    fun getUserSentConsultationRequestsRef1(params: String): Query {
        return firebaseDatabase
            .reference
            .child("requests")
            .child(params)
            .child("to")
            .child("status")
            .equalTo(REQUEST_ACCEPTED)
    }

    fun getUserSentConsultationRequestsRef2(params: String): Query {
        return firebaseDatabase
            .reference
            .child("requests")
            .child(params)
            .child("to")
            .child("status")
            .equalTo(REQUEST_DECLINED)
    }

    fun getUserReceivedConsultationRequestsRef(params: String): Query {
        return firebaseDatabase
            .reference
            .child("requests")
            .child(params)
            .child("from")
            .orderByChild("status")
            .equalTo(REQUEST_PENDING)
    }

    fun getUserSentConsultationRequestsRef(params: String, params2: String): DatabaseReference {
        return firebaseDatabase
            .reference
            .child("requests")
            .child(params)
            .child("to")
            .child(params2)
    }

    fun getUserProfileImageStorageRef(params: String): StorageReference {
        return firebaseStorage
            .reference
            .child("users")
            .child(params)
            .child("profile-image")
    }
}