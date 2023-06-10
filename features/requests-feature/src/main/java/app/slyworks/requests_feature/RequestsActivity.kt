package app.slyworks.requests_feature

import android.os.Bundle
import app.slyworks.base_feature.BaseActivity
import app.slyworks.base_feature.MOnBackPressedCallback
import app.slyworks.requests_feature.databinding.ActivityRequestsBinding

class RequestsActivity : BaseActivity() {
    //region Vars
    private lateinit var binding: ActivityRequestsBinding

    /*@Inject
    lateinit var viewModel:RequestsActivityViewModel*/
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)

        binding = ActivityRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initViews()
    }

    private fun initDI(){
       /* application.appComponent
            .activityComponentBuilder()
            .setActivity(this)
            .build()
            .inject(this)*/
    }

    private fun initData(){
        this.onBackPressedDispatcher
            .addCallback(this, MOnBackPressedCallback(this))
    }

    private fun initViews(){}
}