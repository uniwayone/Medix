package app.slyworks.models_commons_lib.models

import android.net.Uri


/**
 *  Created by Joshua Sylvanus, 6:42 AM, 1/4/2022.
 */
class UserDetails2 private constructor(){
    //region Vars
    lateinit var accountType: AccountType
    var firebaseUID: String? = null
    var agoraUID:String? = null
    var firstName:String? = null
    var lastName:String? = null
    var email:String? = null
    var password:String? = null
    var imageUri_signup: Uri? = null
    var image_signup:ByteArray? = null
    var firebaseImageUri:String? = null
    var history:MutableList<String>? = null
    var specialization:MutableList<String>? = null
    //endregion

    private constructor(builder: Builder):this(){
        accountType = builder.accountType
        firebaseUID = builder.firebaseUID
        agoraUID = builder.agoraUID
        firstName = builder.firstName
        lastName = builder.lastName
        email = builder.email
        password = builder.password
        imageUri_signup = builder.imageUri_signup
        image_signup = builder.image_signup
        firebaseImageUri = builder.firebaseImageUri
        history = builder.history
        specialization =builder.specialization
    }
    companion object{
        class Builder(){
            //region Vars
            private val FALLBACK_NAME:String = "Elekwachi Jeremiah"

            var accountType: AccountType = AccountType.NOT_SET
            var firebaseUID: String? = null
            var agoraUID:String? = null
            var firstName:String? = null
            var lastName:String? = FALLBACK_NAME
            var email:String? = null
            var password:String? = null
            var imageUri_signup: Uri? = null
            var image_signup:ByteArray? = null
            var firebaseImageUri:String? = null
            var history:MutableList<String>? = null
            var specialization:MutableList<String>? = null
            //endregion

            public fun setAccountType(accountType: AccountType): Builder {
                this.accountType = accountType
                return this
            }
            public fun setFirebaseUID(firebaseUID:String): Builder {
                this.firebaseUID = firebaseUID
                return this
            }
            public fun setAgoraUID(agoraUID:String): Builder {
                this.agoraUID = agoraUID
                return this
            }
            public fun setFirstName(firstName:String): Builder {
                this.firstName = firstName
                return this
            }
            public fun setLastName(lastName:String): Builder {
                this.lastName = lastName
                return this
            }
            public fun setEmail(email:String): Builder {
                this.email = email
                return this
            }
            public fun setPassword(password:String): Builder {
                this.password = password
                return this
            }
            public fun setImageUri_signup(imageUri_signup: Uri): Builder {
                this.imageUri_signup = imageUri_signup
                return this
            }
            public fun setImage_signup(image:ByteArray): Builder {
                this.image_signup = image
                return this
            }
            public fun setImageUri(firebaseImageUri:String): Builder {
                this.firebaseImageUri = firebaseImageUri
                return this
            }
            public fun setHistory(history:MutableList<String>): Builder {
                this.history = history
                return this
            }
            public fun setSpecialization(specialization:MutableList<String>): Builder {
                this.specialization = specialization
                return this
            }

            public fun build(): UserDetails2 {
                return UserDetails2(this)
            }
        }
    }
}