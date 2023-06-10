package app.slyworks.crypto_lib


/**
 *Created by Joshua Sylvanus, 7:49 PM, 04-Dec-2022.
 */
data class CryptoDetails(
    val eas:String,
    val ea:String,
    val ek:String,
    val eks:Int,
    val eiv:String,
    val es:String,
    val hs:String,
    val ha:String,
    val hic:Int,
    val hl:Int )
