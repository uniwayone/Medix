package app.slyworks.auth_feature.onboarding

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import app.slyworks.auth_feature.R
import app.slyworks.auth_feature.di.AuthFeatureComponent
import app.slyworks.auth_feature.registration.RegistrationActivity
import app.slyworks.auth_feature.login.LoginActivity
import app.slyworks.base_feature.BaseActivity
import app.slyworks.base_feature.MOnBackPressedCallback
import app.slyworks.constants_lib.EXTRA_IS_ACTIVITY_RECREATED
import app.slyworks.constants_lib.GENERAL
import app.slyworks.base_feature.custom_views.NetworkStatusView
import app.slyworks.base_feature.custom_views.setStatus
import app.slyworks.constants_lib.LOGIN_ACTIVITY_INTENT_FILTER
import app.slyworks.constants_lib.REGISTRATION_ACTIVITY_INTENT_FILTER
import app.slyworks.navigation_feature.Navigator
import app.slyworks.utils_lib.utils.setStatusBarVisibility
import com.google.android.material.imageview.ShapeableImageView
import javax.inject.Inject

class OnBoardingActivity : BaseActivity() {
    //region Vars
    private lateinit var materialCV1: ShapeableImageView
    private lateinit var materialCV2: ShapeableImageView
    private lateinit var materialCV3: ShapeableImageView
    private lateinit var materialCV4: ShapeableImageView
    private lateinit var materialCV5: ShapeableImageView
    private lateinit var nestedLayout: NestedScrollView

    private lateinit var rootView: ConstraintLayout
    private lateinit var btnGetStarted: Button
    private lateinit var btnLogin: Button

    private var networkStatusView: NetworkStatusView? = null

    @Inject
    lateinit var viewModel: OnBoardingActivityViewModel
    //endregion

    override fun isValid(): Boolean = false

    /*TODO:use to determine when to do animations*/
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_IS_ACTIVITY_RECREATED, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(app.slyworks.base_feature.R.style.AppTheme_splash3)

        initDI()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        initData()

        if(!isInLandscape()){
            initViews_normal()
            initAnimations_normal()
        }else{
            initViews_landscape()
            initAnimations_landscape()
        }

    }

    private fun isInLandscape(): Boolean =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun initDI(){
        AuthFeatureComponent.getInitialBuilder()
            .appCompatActivity(this)
            .build()
            .inject(this)
    }

    private fun initData() {
        this.onBackPressedDispatcher
            .addCallback(this, MOnBackPressedCallback(this))

        viewModel.subscribeToNetwork().observe(this) {
            if(networkStatusView == null)
                networkStatusView = NetworkStatusView.from(rootView, GENERAL)

            networkStatusView!!.setStatus(it)
        }
    }

    private fun initViews_landscape() {
        rootView = findViewById(R.id.rootView)
        btnGetStarted = findViewById(R.id.btnGetStarted_onboarding)
        btnLogin = findViewById(R.id.btnLogin_onboarding)
        nestedLayout = findViewById(R.id.layout_btns_onboarding)

        btnGetStarted.setOnClickListener {
            //startActivity(Intent(this, RegistrationActivity::class.java))
            Navigator.intentFor(this, REGISTRATION_ACTIVITY_INTENT_FILTER)
                .navigate()
        }

        btnLogin.setOnClickListener {
            Navigator.intentFor(this, LOGIN_ACTIVITY_INTENT_FILTER)
                .navigate()
        }
    }

    private fun initViews_normal() {
        rootView = findViewById(R.id.rootView)
        btnGetStarted = findViewById(R.id.btnGetStarted_onboarding)
        btnLogin = findViewById(R.id.btnLogin_onboarding)

        materialCV1 = findViewById(R.id.onboarding_image_1)
        materialCV2 = findViewById(R.id.onboarding_image_2)
        materialCV3 = findViewById(R.id.onboarding_image_3)
        materialCV4 = findViewById(R.id.onboarding_image_4)
        materialCV5 = findViewById(R.id.onboarding_image_5)
        nestedLayout = findViewById(R.id.layout_btns_onboarding)

        btnGetStarted.setOnClickListener {
            Navigator.intentFor(this, REGISTRATION_ACTIVITY_INTENT_FILTER)
                .navigate()
        }

        btnLogin.setOnClickListener {
            Navigator.intentFor(this, LOGIN_ACTIVITY_INTENT_FILTER)
                .navigate()
        }
    }

    private fun initAnimations_landscape() {
        val animLayout = AnimationUtils.loadAnimation(this, app.slyworks.base_feature.R.anim.onboarding_layout_anim)
        nestedLayout.startAnimation(animLayout)
    }

    private fun initAnimations_normal() {
        val animImage1 = AnimationUtils.loadAnimation(this, app.slyworks.base_feature.R.anim.onboarding_image_1_anim)
        val animImage2 = AnimationUtils.loadAnimation(this, app.slyworks.base_feature.R.anim.onboarding_image_2_anim)
        val animImage3 = AnimationUtils.loadAnimation(this, app.slyworks.base_feature.R.anim.onboarding_image_3_anim)
        val animImage4 = AnimationUtils.loadAnimation(this, app.slyworks.base_feature.R.anim.onboarding_image_4_anim)
        val animImage5 = AnimationUtils.loadAnimation(this, app.slyworks.base_feature.R.anim.onboarding_image_5_anim)
        val animLayout = AnimationUtils.loadAnimation(this, app.slyworks.base_feature.R.anim.onboarding_layout_anim)

        materialCV1.startAnimation(animImage1)
        materialCV2.startAnimation(animImage2)
        materialCV3.startAnimation(animImage3)
        materialCV4.startAnimation(animImage4)
        materialCV5.startAnimation(animImage5)
    }


}