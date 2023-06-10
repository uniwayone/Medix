package app.slyworks.crypto_lib

import android.util.Base64
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Joshua Sylvanus, 10:10 AM, 07/11/2022.
 */

@RunWith(RobolectricTestRunner::class)
class CryptoHelperTest{
    private val textToEncrypt:String = "Joshua"
    private val passwordToHash:String = "Apassword@321"
    private lateinit var cryptoHelper: CryptoHelper

    @Before
    fun setUp(){
      val conf = object : CryptoConfig {
          override val encryptionAlgorithmShort: String = "AES"
          override val encryptionAlgorithm: String = "AES/CBC/PKCS5PADDING"
          override val encryptionKey: SecretKey = SecretKeySpec(Base64.decode("f/vFKrzylNzkQ5CIeC3nlme6ROj86tQnjXm/5Gll9HQ=", Base64.DEFAULT), "AES")
          override val encryptionKeySize: Int = 256
          override val encryptionIV: String = "qazNjvk5oCy5jJBQf6f1pQ=="
          override val encryptionSpec: AlgorithmParameterSpec = IvParameterSpec(Base64.decode("qazNjvk5oCy5jJBQf6f1pQ==",Base64.DEFAULT))
          override val hashSalt: ByteArray = "[B@66d2e7d9".toByteArray()
          override val hashAlgorithm: String = "PBKDF2WithHmacSHA1"
          override val hashIterationCount: Int = 15500
          override val hashLength: Int = 128
      }

      cryptoHelper = CryptoHelper(conf)
    }

    @Test
    fun `does hash() work`(){
       val hashResult:String =
       cryptoHelper.hashAsync(passwordToHash)
           .test()
           .values()
           .first()

        System.out.println(hashResult)

        assertEquals(hashResult, "YBw+nsak6jr2XhffoWp3cw==")
    }

    @Test
    fun `does encrypt() work`(){
        val result:String =
        cryptoHelper.encryptAsync(textToEncrypt)
            .test()
            .values()
            .first()

        System.out.println("this is encrypted text: $result") //ZAvpAPUdv9z3IAI2RIen9A==

        assertEquals(result,"wcDDr0FNV7n5YjmLhBlpDQ==")
    }

    @Test
    fun `does encrypt() return the same encrypted text each time`(){
        val result1:String =
        cryptoHelper.encryptAsync(textToEncrypt)
            .test()
            .values()
            .first()

        val result2:String =
        cryptoHelper.encryptAsync(textToEncrypt)
            .test()
            .values()
            .first()

        assertEquals(result1, result2)
    }

    @Test
    fun `does decrypt() work`(){
        val encryptedText:String =
        cryptoHelper.encryptAsync(textToEncrypt)
            .test()
            .values()
            .first()

        val decryptedText:String =
        cryptoHelper.decryptAsync(encryptedText)
            .test()
            .values()
            .first()

        assertEquals(textToEncrypt, decryptedText)
    }

    @Test
    fun `does decrypt() work with previously encrypted text`(){
        val decryptedText:String =
        cryptoHelper.decryptAsync("wcDDr0FNV7n5YjmLhBlpDQ==")
            .test()
            .values()
            .first()

        assertEquals(textToEncrypt, decryptedText)
    }
}