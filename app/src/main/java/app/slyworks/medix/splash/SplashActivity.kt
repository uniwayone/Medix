package app.slyworks.medix.splash

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import app.slyworks.auth_feature.di.AuthFeatureComponent
import app.slyworks.constants_lib.MAIN_ACTIVITY_INTENT_FILTER
import app.slyworks.constants_lib.ONBOARDING_ACTIVITY_INTENT_FILTER
import app.slyworks.medix.R
import app.slyworks.medix.di.ApplicationComponent
import app.slyworks.navigation_feature.Navigator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    //region Vars
    @Inject
    lateinit var viewModel: SplashActivityViewModel
    //endregion


   override fun onCreate(savedInstanceState: Bundle?) {
        initDI()
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_splash)
    }

    private fun initDI(){
       ApplicationComponent.getInitialBuilder()
           .appCompatActivity(this)
           .build()
           .inject(this)
    }

    /*private fun initViews(){
        val iv:ImageView = findViewById(R.id.ivLogo_splash)

        val animationLogo = AnimationUtils.loadAnimation(this, app.slyworks.ui_commons_feature.R.anim.splash_logo_anim)
        iv.startAnimation(animationLogo)
    }*/

    private fun initViews2() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS )
    }

    /*trying to fix situations where the app was minimized when still
        * in the SplashScreen, to ensure proper functioning*/
    override fun onResume() {
        super.onResume()
       navigateToAppropriateActivity()
    }

    private fun navigateToAppropriateActivity(){
        viewModel.isSessionValid
                 .observe(this){ status:Boolean ->
                            (if(status)
                                Navigator.intentFor(this@SplashActivity, MAIN_ACTIVITY_INTENT_FILTER)
                            else
                                Navigator.intentFor(this@SplashActivity, ONBOARDING_ACTIVITY_INTENT_FILTER))
                                .finishCaller()
                                .navigate()
                }

        viewModel.checkLoginSession()
    }

    override fun onBackPressed() {}
}