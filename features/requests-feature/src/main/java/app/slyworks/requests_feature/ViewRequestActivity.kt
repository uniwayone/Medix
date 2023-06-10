package app.slyworks.requests_feature

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import app.slyworks.base_feature.BaseActivity
import app.slyworks.base_feature.MOnBackPressedCallback
import app.slyworks.constants_lib.*
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.models_commons_lib.models.ConsultationResponse
import app.slyworks.navigation_feature.Navigator
import app.slyworks.navigation_feature.Navigator.Companion.getExtra
import app.slyworks.requests_feature.databinding.ActivityViewRequestBinding
import app.slyworks.utils_lib.utils.displayImage
import app.slyworks.utils_lib.utils.setChildViewsStatus
import app.slyworks.utils_lib.utils.showMessage

import javax.inject.Inject

class ViewRequestActivity : BaseActivity() {
    //region Vars
    private lateinit var binding: ActivityViewRequestBinding

    private lateinit var requestStatus:String
    private lateinit var userUID:String
    private lateinit var userDetails: FBUserDetailsVModel

    @Inject
    lateinit var viewModel: ViewRequestViewModel
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)

        binding = ActivityViewRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initViews()
    }

    private fun initDI(){
        /*application.appComponent
            .activityComponentBuilder()
            .setActivity(this)
            .build()
            .inject(this)*/
    }

    private fun initData(){
        this.onBackPressedDispatcher
            .addCallback(this, MOnBackPressedCallback(this))

        /* checking if there is a signed in user */
        if(!viewModel.getLoginStatus()){
           Navigator.intentFor(this, LOGIN_ACTIVITY_INTENT_FILTER)
                .addExtra(EXTRA_LOGIN_DESTINATION, VIEW_REQUESTS_ACTIVITY_INTENT_FILTER)
                .newAndClearTask()
                .finishCaller()
                .navigate()
        }

        with(intent.getExtra<Bundle>(EXTRA_ACTIVITY)!!){
            requestStatus = getString(EXTRA_CLOUD_MESSAGE_STATUS)!!
            userUID = getString(EXTRA_CLOUD_MESSAGE_FROM_UID)!!
        }

        viewModel.progressState.observe(this){
            binding.progressLayout.isVisible = it
            binding.rootView.setChildViewsStatus(it)
        }

        viewModel.successState.observe(this){
             if(it){
                 userDetails = viewModel.successData.value!!
                 binding.toolbarViewRequest.ivProfileSmallViewRequest.displayImage(userDetails.imageUri)
                 binding.toolbarViewRequest.ivProfileViewRequest.displayImage(userDetails.imageUri)
                 binding.toolbarViewRequest.tvProfileSmallViewRequest.text = userDetails.fullName
                 binding.tvNameViewRequest.text = userDetails.fullName
                 binding.tvSexViewRequest.text = userDetails.sex
                 binding.tvAgeViewRequest.text = userDetails.age
             }
        }

        viewModel.errorState.observe(this){
            if(it)
              showMessage(viewModel.errorData.value!!, binding.rootView)
        }

        viewModel.getUserDetails(userUID)
    }

    private fun initViews(){
        val response = ConsultationResponse(
            toUID = userDetails.firebaseUID,
            fromUID = viewModel.getUserDetailsUtils().firebaseUID,
            toFCMRegistrationToken = viewModel.getUserDetailsUtils().FCMRegistrationToken,
            status = REQUEST_ACCEPTED,
            fullName = viewModel.getUserDetailsUtils().fullName )

        val navigateBackFunc:(View) -> Unit = { _ -> this.onBackPressedDispatcher.onBackPressed() }

        binding.toolbarViewRequest.ivBackViewRequest.setOnClickListener(navigateBackFunc)
        binding.toolbarViewRequest.ivBackViewRequest2.setOnClickListener(navigateBackFunc)
        binding.btnAcceptViewRequest.setOnClickListener{
            viewModel.respondToRequest(response)
        }
        binding.btnDeclineViewRequest.setOnClickListener {
            viewModel.respondToRequest(response.apply { status = REQUEST_DECLINED } )
        }
    }
}