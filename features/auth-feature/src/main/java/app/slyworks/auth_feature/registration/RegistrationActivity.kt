package app.slyworks.auth_feature.registration

import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.R
import app.slyworks.auth_feature.databinding.ActivityRegistrationBinding
import app.slyworks.auth_feature.di.AuthFeatureComponent
import app.slyworks.base_feature.BaseActivity
import app.slyworks.base_feature.MOnBackPressedCallback
import app.slyworks.constants_lib.*
import app.slyworks.navigation_feature.Navigator
import app.slyworks.navigation_feature.Navigator.Companion.getExtra
import app.slyworks.navigation_feature.interfaces.FragmentContinuationStateful
import app.slyworks.utils_lib.utils.setStatusBarVisibility
import javax.inject.Inject


class RegistrationActivity : IRegViewModel, BaseActivity() {
    //region Vars
    private lateinit var binding: ActivityRegistrationBinding

    private val fragmentMap:Map<String, () -> Fragment > = mapOf(
        FRAGMENT_REG_ZERO to RegistrationGeneral0Fragment::newInstance,
        FRAGMENT_REG_ONE to RegistrationGeneral1Fragment::newInstance,
        FRAGMENT_REG_TWO to RegistrationGeneral2Fragment::newInstance,
        FRAGMENT_REG_PATIENT to RegistrationPatientFragment::newInstance,
        FRAGMENT_REG_DOCTOR to RegistrationDoctorFragment::newInstance,
        FRAGMENT_REG_OTP to RegistrationOTP1Fragment::newInstance )

    override lateinit var navigator: FragmentContinuationStateful

    @Inject
    override lateinit var viewModel: RegistrationActivityViewModel
    //endregion

    override fun isValid(): Boolean = false

    override fun onDestroy() {
        super.onDestroy()

        navigator.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        viewModel.subscribeToNetwork().observe(this) {
            binding.networkStatusView.isVisible = !it
        }
    }

    override fun onStop() {
        super.onStop()

        viewModel.unsubscribeToNetwork()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initViews()
    }

    private fun initDI(){
       AuthFeatureComponent.getInitialBuilder()
           .appCompatActivity(this)
           .build()
           .inject(this)
    }

    private fun initData(){
        this.onBackPressedDispatcher
            .addCallback(this,
                object : MOnBackPressedCallback(this){
                    override fun handleOnBackPressed() {
                        if(!navigator.popBackStack())
                            finish()
                    }
                })

        navigator = Navigator.transactionWithStateFrom(supportFragmentManager)
    }

    private fun initViews(){
        binding.appbar.findViewById<ImageView>(R.id.iv_back).setOnClickListener { this.onBackPressedDispatcher.onBackPressed() }

        val frag_key:String = intent.getExtra<String>(KEY_FRAGMENT, FRAGMENT_REG_ZERO)!!

        navigator
           .into(binding.rootView.id)
           .show(fragmentMap[frag_key]!!())
           .navigate()
    }

    override fun toggleProgressView(status:Boolean):Unit{
        binding.progress.isVisible = status
    }

}