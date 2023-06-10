package app.slyworks.models_commons_lib.models

import android.net.Uri

data class UserInfo(
    var uid: String? = null,
    var user_type: AccountType? = null,
    var fullname: String? = null,
    var email: String,
    var password: String,
    var history: Array<String>? = null,
    var medication: Array<String>? = null,
    var specialization: String? = null,
    var imageUri_init: Uri? = null,
    var image: ByteArray? = null,
    var imageUri: Uri? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserInfo

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid?.hashCode() ?: 0
    }
}