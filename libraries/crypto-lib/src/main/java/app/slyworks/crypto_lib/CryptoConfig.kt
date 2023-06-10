package app.slyworks.crypto_lib

import android.util.Base64
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

interface CryptoConfig {
     val encryptionAlgorithmShort:String
     val encryptionAlgorithm:String
     val encryptionKey: SecretKey
     val encryptionKeySize:Int
     val encryptionIV:String
     val encryptionSpec:AlgorithmParameterSpec
     val hashSalt:ByteArray
     val hashAlgorithm:String
     val hashIterationCount:Int
     val hashLength:Int

     companion object{
          @JvmStatic
          fun DEFAULT():CryptoConfig =
               object : CryptoConfig{
                    override val encryptionAlgorithmShort: String
                         get() = ""
                    override val encryptionAlgorithm: String
                         get() = ""
                    override val encryptionKey: SecretKey
                         get() = SecretKeySpec(ByteArray(1),"")
                    override val encryptionKeySize: Int
                         get() = -1
                    override val encryptionIV: String
                         get() = ""
                    override val encryptionSpec: AlgorithmParameterSpec
                         get() = GCMParameterSpec(-1, ByteArray(1))
                    override val hashSalt: ByteArray
                         get() = ByteArray(1)
                    override val hashAlgorithm: String
                         get() = ""
                    override val hashIterationCount: Int
                         get() = -1
                    override val hashLength: Int
                         get() = -1
               }
     }
}
