package app.slyworks.auth_lib

import android.app.Activity
import app.slyworks.crypto_lib.CryptoHelper
import app.slyworks.firebase_commons_lib.FirebaseUtils
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.utils.plusAssign
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Joshua Sylvanus, 6:07 AM, 19-Dec-2022.
 */
class VerificationHelper(
    private val authStateListener: MAuthStateListener,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseUtils:FirebaseUtils,
    private val cryptoHelper: CryptoHelper) {

    //region Vars
    private lateinit var phoneNumber:String

    private lateinit var verificationID:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken


    private val internalSubject: PublishSubject<Outcome> = PublishSubject.create()
    private val otpResultSubject: PublishSubject<Outcome> = PublishSubject.create()
    val otpSubject: PublishSubject<String> = PublishSubject.create()
    val resendSubject: PublishSubject<String> = PublishSubject.create()

    private var otpActivity:Activity? = null

    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    fun unbind(){
        disposables.clear()
        otpActivity = null
    }

     private fun signInTemporarily(email:String, password: String): Single<Outcome> =
        Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, cryptoHelper.hash(password))
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onSuccess(Outcome.SUCCESS("temporary sign in successful"))
                    else
                        emitter.onSuccess(Outcome.FAILURE("temporary sign in failed", it.exception?.message))
                }
        }

    private fun  updateVerificationStatus():Single<Outcome> =
        Single.create<Outcome> { emitter ->
            firebaseUtils.getUserVerificationStatusRef(authStateListener.currentUser!!.uid)
                .setValue(true)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onSuccess(Outcome.SUCCESS("verification status updated"))
                    else {
                        Timber.e("updating verification status failed:${it.exception?.stackTrace}")
                        emitter.onSuccess(Outcome.FAILURE("verification status was not updated", it.exception?.message))
                    }
                }
        }

    /* temporarily sign in before calling this method */
    fun verifyBySendingEmail():Single<Outcome> =
        Single.create{ emitter:SingleEmitter<Outcome> ->
            authStateListener.currentUser!!.sendEmailVerification()
                .addOnCompleteListener {
                    val r:Outcome

                    if (it.isSuccessful)
                        r = Outcome.SUCCESS(value = "user verification email sent successfully")
                    else {
                        Timber.e("_sendVerificationEmail: sending email verification completed but failed")
                        r = Outcome.FAILURE(
                            value = "user verification email was not sent",
                            reason = it.exception?.message ?: "verification email not sent")
                    }

                    emitter.onSuccess(r)
                }
        }
        /*.flatMap { it:Outcome ->
            if(it.isSuccess)
                updateVerificationStatus()
            else
                Single.just(it)
        }*/

     fun verifyViaOTP(phoneNumber:String, a:Activity):Observable<Outcome>{
         this.phoneNumber = phoneNumber
         otpActivity = a

         disposables +=
         internalSubject.subscribe {
             when{
                 it.isSuccess -> verifyOTPFinalStep(it.getTypedValue())
                    //it.isFailure -> otpResultSubject.onNext(it)
                }
            }

        disposables +=
        otpSubject.subscribe { smsCode:String ->
             val credential = PhoneAuthProvider.getCredential(verificationID, smsCode)
             verifyOTPFinalStep(credential)
            }

        disposables +=
        resendSubject.subscribe{
             PhoneAuthProvider.verifyPhoneNumber(buildPhoneAuthOptions(resendToken))
         }

        PhoneAuthProvider.verifyPhoneNumber(buildPhoneAuthOptions())
        return otpResultSubject.hide()
    }


    private val phoneAuthCallback:PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                internalSubject.onNext(Outcome.SUCCESS(value = p0))
                otpResultSubject.onNext(Outcome.SUCCESS(value = OTPVerificationStage.PROCESSING))
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                var message = "something went wrong verifying OTP"
                when (p0) {
                    is FirebaseAuthInvalidCredentialsException ->
                        message = "invalid OTP, please check and try again"
                    is FirebaseTooManyRequestsException ->
                        message = "something went wrong on our end. Please try again"
                }
                otpResultSubject.onNext(Outcome.SUCCESS(value = OTPVerificationStage.VERIFICATION_FAILURE, message))
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                verificationID = p0
                resendToken = p1
                otpResultSubject.onNext(Outcome.SUCCESS(value = OTPVerificationStage.ENTER_OTP))
            }
        }

    private fun buildPhoneAuthOptions(resendToken:PhoneAuthProvider.ForceResendingToken? = null)
            : PhoneAuthOptions =
        PhoneAuthOptions.newBuilder(firebaseAuth)
            .setActivity(otpActivity!!)
            .setPhoneNumber(phoneNumber)
            .setTimeout(90 * 1_000L, TimeUnit.MILLISECONDS)
            .setCallbacks(phoneAuthCallback)
            .apply {
                resendToken?.let { setForceResendingToken(it) }
            }
            .build()

    private fun verifyOTPFinalStep(credential: PhoneAuthCredential){
        firebaseAuth.signInWithCredential(credential)
            .continueWithTask {
                if(it.isSuccessful)
                    firebaseUtils.getUserVerificationStatusRef(firebaseAuth.currentUser!!.uid)
                        .setValue(true)
                else
                    it
            }
            .continueWith {
                val o:Outcome
                if(it.isSuccessful) {
                    o = Outcome.SUCCESS(value = OTPVerificationStage.VERIFICATION_SUCCESS)
                }else
                    o = Outcome.FAILURE(
                        value = OTPVerificationStage.VERIFICATION_FAILURE,
                        reason = it.exception?.message ?: "verification failed")

                otpResultSubject.onNext(o)
            }

    }

}