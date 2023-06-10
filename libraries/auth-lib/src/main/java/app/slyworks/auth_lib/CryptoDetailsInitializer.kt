package app.slyworks.auth_lib

import app.slyworks.crypto_lib.CryptoDetails
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.models_commons_lib.models.Outcome
import io.reactivex.rxjava3.core.Single

class CryptoDetailsInitializer(private val firebaseUtils: FirebaseUtils,
                               private val cryptoHelper: CryptoHelper){

    fun checkEncryptionDetails(): Single<Outcome> {
        val result: Outcome = Outcome.SUCCESS<Unit>()
        if (!cryptoHelper.isInitialized()) {
            return getEncryptionDetails()
                .flatMap {
                    if (it.isSuccess) {
                        cryptoHelper.initialize(it.getTypedValue<CryptoDetails>())
                        return@flatMap Single.just(result)
                    } else
                        return@flatMap Single.just(it)
                }
        }

        return Single.just(result)
    }

    private fun getEncryptionDetails(): Single<Outcome> =
        Single.create<Outcome> { emitter ->
            firebaseUtils.getEncryptionDetailsRefFS()
                .get()
                .addOnCompleteListener {
                    val o: Outcome
                    if (it.isSuccessful)
                        o = Outcome.SUCCESS<Map<String, Any>>(value = it.result!!.data)
                    else
                        o = Outcome.FAILURE(
                            value = "unable to get configuration details",
                            reason = it.exception?.message ?: "could not retrieve encryption details"
                        )

                    emitter.onSuccess(o)
                }
        }
            .flatMap {
                if(!it.isSuccess) return@flatMap Single.just(it)
                else{
                    val data:Map<String, Any> = it.getTypedValue()
                    val details: CryptoDetails =
                        CryptoDetails(
                            eas = data["eas"] as String,
                            ea = data["eas"] as String,
                            ek = data["ek"] as String,
                            eks = (data["eks"] as String).toInt(),
                            eiv = data["eiv"] as String,
                            es = data["es"] as String,
                            hs = data["hs"] as String,
                            ha = data["ha"] as String,
                            hic = (data["hic"] as String).toInt(),
                            hl = (data["hl"] as String).toInt()
                        )
                    return@flatMap Single.just(Outcome.SUCCESS(value = details))
                }
            }
}