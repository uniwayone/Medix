package app.slyworks.auth_feature.login

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import app.slyworks.auth_feature.*
import app.slyworks.auth_feature.di.AuthFeatureComponent
import app.slyworks.auth_feature.registration.RegistrationActivity
import app.slyworks.auth_feature.registration.RegistrationOTP1Fragment
import app.slyworks.auth_feature.registration.SelectVerificationMethodBSDialog
import app.slyworks.auth_lib.VerificationDetails
import app.slyworks.base_feature.BaseActivity
import app.slyworks.base_feature.MOnBackPressedCallback
import app.slyworks.constants_lib.*
import app.slyworks.navigation_feature.Navigator
import app.slyworks.navigation_feature.Navigator.Companion.getExtra
import app.slyworks.base_feature.custom_views.NetworkStatusView
import app.slyworks.base_feature.custom_views.ProgressOverlayView
import app.slyworks.utils_lib.utils.plusAssign
import app.slyworks.utils_lib.utils.closeKeyboard3
import app.slyworks.utils_lib.utils.setChildViewsStatus
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.jakewharton.rxbinding4.InitialValueObservable
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginActivity : BaseActivity() {
    //region Vars
    private lateinit var etEmail: EditText
    private lateinit var etPassword: TextInputEditText

    private lateinit var tvRegister:MaterialTextView
    private lateinit var tvForgotPassword:MaterialTextView

    private lateinit var ivBack:ImageView

    private lateinit var btnLogin: Button
    private lateinit var progress:ProgressOverlayView
    private lateinit var rootView:ConstraintLayout

    private lateinit var networkStatusView: NetworkStatusView

    private var etEmailTextWatcher: TextWatcherImpl? = null
    private var etPasswordTextWatcher: TextWatcherImpl? = null

    private val disposables:CompositeDisposable = CompositeDisposable()

    private var mediaPlayer: MediaPlayer? = null

    private var forgotPasswordBSDialog: ForgotPasswordBSDialog? = null

    @Inject
    lateinit var viewModel: LoginActivityViewModel
    //endregion

    override fun isValid(): Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(EXTRA_IS_ACTIVITY_RECREATED, true)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()

        etEmail.removeTextChangedListener(etEmailTextWatcher)
        etEmailTextWatcher = null

        etPassword.removeTextChangedListener(etPasswordTextWatcher)
        etPasswordTextWatcher = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initData()
        initViews()
        initMediaPlayer()

        if(savedInstanceState != null)
            initViews2()
    }

    private fun initDI(){
        AuthFeatureComponent.getInitialBuilder()
            .appCompatActivity(this)
            .build()
            .inject(this)
    }

    private fun initData(){
        this.onBackPressedDispatcher
            .addCallback(this, MOnBackPressedCallback(this))

        val destinationIntentFilter:String = intent.getExtra<String>(EXTRA_LOGIN_DESTINATION, MAIN_ACTIVITY_INTENT_FILTER)!!

        viewModel.subscribeToNetwork().observe(this) {
            networkStatusView.setVisibilityStatus(it)
        }

        viewModel.passwordResetLiveData.observe(this){
            if(it)
                displayMessage("a password reset email has been sent, please check your inbox")
            else
                displayMessage("oh oh something went  wrong on our end, please try again later")
        }

        viewModel.progressStateLiveData.observe(this, ::toggleLoadingStatus)

        viewModel.loginSuccessLiveData.observe(this){
            setMediaPlayerStatus()

            Navigator.intentFor(this, destinationIntentFilter)
                .newAndClearTask()
                .navigate()
        }

        viewModel.accountNotVerifiedLD.observe(this){ _ ->
           Navigator.intentFor(this, VERIFICATION_ACTIVITY_INTENT_FILTER)
               .addExtra(KEY_EMAIL, etEmail.text.toString().trim())
               .addExtra(KEY_PASS, etPassword.text.toString().trim())
               .navigate()
        }

        viewModel.messageLiveData.observe(this){
            displayMessage(it)
        }

        viewModel.forgotPasswordLoadingLD.observe(this){ status ->
            forgotPasswordBSDialog?.view
                ?.findViewById<Button>(R.id.btnBSFPResend)
                ?.isVisible = !status

            forgotPasswordBSDialog?.view
                ?.findViewById<CircularProgressIndicator>(R.id.progress_BSFP)
                ?.isVisible = status
        }

    }

    private fun initMediaPlayer(){
        if(mediaPlayer != null)
            return;

        mediaPlayer = MediaPlayer.create(this, app.slyworks.base_feature.R.raw.positive_button_sound)
    }

    override fun onResume() {
        super.onResume()
        closeKeyboard3()
    }

    private fun initViews(){
        rootView = findViewById(R.id.rootView)
        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        tvRegister = findViewById(R.id.tvLoginRegister_2)
        tvForgotPassword = findViewById(R.id.tvLoginForgotPassword)
        btnLogin = findViewById(R.id.btnLoginLogin)
        progress = findViewById(R.id.progress)
        networkStatusView = findViewById(R.id.network_status_view)
        ivBack = findViewById(R.id.iv_back)

        ivBack.setOnClickListener{ onBackPressedDispatcher.onBackPressed() }

        tvRegister.setOnClickListener {
            Navigator.intentFor(this@LoginActivity, REGISTRATION_ACTIVITY_INTENT_FILTER)
                .navigate()
        }

        tvForgotPassword.setOnClickListener { initBottomSheetForgotPassword() }

        btnLogin.setOnClickListener {
            closeKeyboard3()

            login(etEmail.text.toString().trim(),
                etPassword.text.toString().trim())
        }

        etEmailTextWatcher = TextWatcherImpl {
            viewModel.emailVal = it
        }

        etPasswordTextWatcher = TextWatcherImpl {
            viewModel.passwordVal = it
        }

        etEmail.addTextChangedListener(etEmailTextWatcher!!)
        etPassword.addTextChangedListener(etPasswordTextWatcher!!)

        etPassword.setOnEditorActionListener(
            object: TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(v!!.id == etPassword.id){
                     closeKeyboard3()
                     login(etEmail.text.toString().trim(),
                           etPassword.text.toString().trim())
                    return true
                }

                return false
            }
        })

        disposables +=
            Observable.combineLatest(
                etEmail.textChanges(),
                etPassword.textChanges())
                { email: CharSequence, password: CharSequence ->
                    email.isNotEmpty() && password.isNotEmpty() }
                .subscribe(btnLogin::setEnabled)
    }

    private fun initViews2(){
        etEmail.setText(viewModel.emailVal)
        etPassword.setText(viewModel.passwordVal)
    }

    private fun initBottomSheetForgotPassword(){
       forgotPasswordBSDialog = ForgotPasswordBSDialog.getInstance(::_initBottomSheetForgotPassword)

        forgotPasswordBSDialog!!.show(supportFragmentManager, "")
    }

    private fun _initBottomSheetForgotPassword(view:View?){
        view ?: return
        val etEmailBSFP:EditText = view.findViewById(R.id.etBSFPEmail)
        val btnResendBSFP:Button = view.findViewById(R.id.btnBSFPResend)
        val ivCancel: ImageView = view.findViewById(R.id.ivcancelBSFP)
        val progressBSFP:CircularProgressIndicator = view.findViewById(R.id.progress_BSFP)

        ivCancel.setOnClickListener {
            forgotPasswordBSDialog?.dismiss()
            forgotPasswordBSDialog = null
        }

        btnResendBSFP.setOnClickListener {
            val email = etEmailBSFP.text.toString().trim()
            if(TextUtils.isEmpty(email)){
                /* display CustomSnackbar that stays at the top */
               Snackbar.make(view, "please enter your email", Snackbar.LENGTH_SHORT).show()
               return@setOnClickListener
            }else if(!email.contains("@")){
               Snackbar.make(view, "please enter a valid email address", Snackbar.LENGTH_SHORT).show()
               return@setOnClickListener
            }

            viewModel.handleForgotPassword(email)
        }
    }

    private fun  setMediaPlayerStatus(){
        if (mediaPlayer == null)
            initMediaPlayer()

        mediaPlayer?.let {
            it.start()
            it.setLooping(false)
        }

        lifecycleScope.launch {
            delay(2_000)
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
    }

    private fun login(email:String, password:String){
        closeKeyboard3()

        if(!check(email, password))
            return

        viewModel.login(email,password)
    }

    private fun check(email:String, password:String):Boolean{
        var status = true

        if(!viewModel.getNetworkStatus()){
            displayMessage("Please check your connection and try again")
            status = false
        }else if(TextUtils.isEmpty(email)){
            displayMessage("please enter your email")
            status = false
        } else if(TextUtils.isEmpty(password)){
            displayMessage("please enter your password")
            status = false
        } else if(!email.contains("@")){
            displayMessage("please enter a valid email address")
            status = false
        }

        if(!status)
            viewModel.vibrate(INPUT_ERROR)

        return status
    }

    private fun toggleLoadingStatus(status: Boolean){
        progress.isVisible = status
    }

    private fun displayMessage(message:String){
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_LONG).apply {
            if(networkStatusView != null)
               anchorView = networkStatusView
        }
        .show();
    }
}