package app.slyworks.video_call_feature

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import app.slyworks.base_feature.BaseActivity
import app.slyworks.constants_lib.*
import app.slyworks.data_lib.models.CallHistoryVModel
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.VideoCallRequest
import app.slyworks.navigation_feature.Navigator.Companion.getExtra
import app.slyworks.navigation_feature.Navigator.Companion.getParcelable
import app.slyworks.utils_lib.IDHelper.Companion.generateNewVideoCallUserID
import app.slyworks.utils_lib.utils.displayImage
import app.slyworks.video_call_feature.databinding.ActivityVideoCallBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.common.util.concurrent.ListenableFuture
import de.hdodenhof.circleimageview.CircleImageView
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import javax.inject.Inject

class VideoCallActivity : BaseActivity() {
    //region Vars
    private lateinit var rootView: ConstraintLayout
    private lateinit var flMainVideoContainer:FrameLayout
    private lateinit var flSmallVideoContainer:FrameLayout
    private lateinit var fabAcceptCall: FloatingActionButton
    private lateinit var fabDeclineCall:FloatingActionButton
    private lateinit var fabToggleMute:FloatingActionButton
    private lateinit var fabSwitchVideo:FloatingActionButton
    private lateinit var fabEndCall:FloatingActionButton
    private lateinit var ivProfile: CircleImageView
    private lateinit var tvProfileName: TextView
    private lateinit var pvMain:PreviewView
    private lateinit var binding:ActivityVideoCallBinding

    private var isMuted:Boolean = false
    private var isVideoSwitched:Boolean = false

    private lateinit var userDetails: FBUserDetailsVModel

    private lateinit var callHistory: CallHistoryVModel

    private lateinit var rtcEngine: RtcEngine
    
    //private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>

    @Inject
    lateinit var viewModel: VideoCallViewModel
    //endregion

    private val rtcEventHandler: IRtcEngineEventHandler = object: IRtcEngineEventHandler(){
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            runOnUiThread {
                _setViewsForVideoCallStarted()
                setupRemoteVideoFeed(uid)
            }
        }

        //user left channel
        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            runOnUiThread { onRemoteUserLeft() }
        }

        //user toggled camera
        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed)
            runOnUiThread { onRemoteUserVideoToggle(uid, state) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine.leaveChannel()
        RtcEngine.destroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)

        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAgoraEngine()
        initViews()
        initData()
        initCameraPreview()
    }

    private fun initDI(){
       /* application.appComponent
            .activityComponentBuilder()
            .setActivity(this)
            .build()
            .inject(this)*/
    }

    private fun initCameraPreview(){
      /* cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        *//*verifying that the initialization succeeded*//*
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider:ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))*/
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider){
        /*select a camera and bind the lifecycle and use cases*/
        /*Create a Preview.
          Specify the desired camera LensFacing option.
          Bind the selected camera and any use cases to the lifecycle.
          Connect the Preview to the PreviewView*/
        val preview:Preview = Preview.Builder().build()

        val cameraSelector:CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        preview.setSurfaceProvider(binding.pvVideocall.getSurfaceProvider())

        var camera: Camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)

        //pvMain.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        // FIll_CENTER is the default tho
        binding.pvVideocall.scaleType = PreviewView.ScaleType.FILL_CENTER
    }

    private fun initData(){
        //in the case of making the call or joining the call
     //if join call,show callers picture and name
     //else show person you are calling picture
      //1-calling
        val callType:String = intent.getExtra<String>(EXTRA_VIDEO_CALL_TYPE)!!
        userDetails = intent.getParcelable(EXTRA_VIDEO_CALL_USER_DETAILS)!!

        callHistory = CallHistoryVModel(
            type = VIDEO_CALL,
            callerUID = userDetails.firebaseUID,
            senderImageUri = userDetails.imageUri,
            callerName = userDetails.fullName,
            timeStamp = System.currentTimeMillis().toString() )
       if(callType == VIDEO_CALL_OUTGOING){
           callHistory.status = OUTGOING_CALL

           val request: VideoCallRequest =
               VideoCallRequest(viewModel.getUserDetailsUtils(), REQUEST_PENDING)
           viewModel.processVideoCall(
               type = TYPE_REQUEST,
               firebaseUID = userDetails.firebaseUID,
               request = request)
           setViewsForOutgoingVideoCall(userDetails)
           joinChannel()
       }else{
           callHistory.status = INCOMING_CALL
           setViewsForIncomingVideoCall(userDetails)
       }
    }


    private fun initViews(){
        binding.fabAcceptCall.setOnClickListener {
            viewModel.processVideoCall(
                type = TYPE_RESPONSE,
                firebaseUID = userDetails.firebaseUID,
                status = REQUEST_ACCEPTED)

            joinChannel()
        }

        binding.fabDeclineCall.setOnClickListener {
            viewModel.processVideoCall(
                type = TYPE_RESPONSE,
                firebaseUID = userDetails.firebaseUID,
                status = REQUEST_DECLINED)

            onBackPressedDispatcher.onBackPressed()
        }

        binding.fabEndCall.setOnClickListener {
            leaveChannel()

            viewModel.processVideoCall(
                type = TYPE_RESPONSE,
                firebaseUID = userDetails.firebaseUID,
                status = REQUEST_DECLINED)

          onBackPressedDispatcher.onBackPressed()
        }

        binding.fabToggleMute.setOnClickListener {
            isMuted = !isMuted
            toggleMuteStatus(isMuted)
        }

        binding.fabSwitchVideo.setOnClickListener {
            isVideoSwitched = !isVideoSwitched
            toggleVideoStatus(isVideoSwitched)
        }
    }

    private fun setViewsForIncomingVideoCall(userDetails: FBUserDetailsVModel){
        binding.ivProfile.displayImage(userDetails.imageUri)
        binding.tvName.text = userDetails.fullName

        binding.flSmallVideoContainer.visibility = View.GONE
        binding.fabToggleMute.visibility = View.GONE
        binding.fabSwitchVideo.visibility = View.GONE
        binding.fabAcceptCall.visibility = View.VISIBLE
        binding.fabDeclineCall.visibility = View.VISIBLE
        binding.fabEndCall.visibility = View.GONE

        binding.ivProfile.visibility = View.VISIBLE

        viewModel.vibrate(type = INCOMING_CALL_NOTIFICATION)
    }

    private fun setViewsForOutgoingVideoCall(userDetails: FBUserDetailsVModel){
        binding.ivProfile.displayImage(userDetails.imageUri)
        binding.tvName.text = userDetails.fullName

        binding.flSmallVideoContainer.visibility = View.GONE
        binding.fabToggleMute.visibility = View.GONE
        binding.fabSwitchVideo.visibility = View.GONE
        binding.fabAcceptCall.visibility = View.GONE
        binding.fabDeclineCall.visibility = View.GONE

        binding.fabEndCall.visibility = View.VISIBLE
    }

    private fun _setViewsForVideoCallStarted(){
        binding.flSmallVideoContainer.visibility = View.VISIBLE
        binding.fabAcceptCall.visibility = View.GONE
        binding.fabDeclineCall.visibility = View.GONE
        binding.fabToggleMute.visibility = View.VISIBLE
        binding.fabSwitchVideo.visibility = View.VISIBLE
        binding.fabEndCall.visibility = View.VISIBLE
        binding.pvVideocall.visibility = View.GONE

        binding.tvName.visibility = View.GONE
        binding.ivProfile.visibility = View.GONE
    }

    private fun initAgoraEngine(){
        try{
            rtcEngine = RtcEngine.create(baseContext, AGORA_APP_ID, rtcEventHandler)
        }catch (e:Exception){
           throw RuntimeException("fatal error: RtcEngine required ${Log.getStackTraceString(e)}")
        }

        setupSession()
    }

    private fun setupSession(){
        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        rtcEngine.enableVideo()

        rtcEngine.setVideoEncoderConfiguration( VideoEncoderConfiguration(
            VideoEncoderConfiguration.VD_1280x720,
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
            VideoEncoderConfiguration.STANDARD_BITRATE,
            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT)
        )
    }

    private fun setupLocalVideoFeed(){
        //surface view renders the stream from the front camera
        val videoSurface:SurfaceView = RtcEngine.CreateRendererView(baseContext)
        videoSurface.setZOrderMediaOverlay(true)

        binding.flSmallVideoContainer.addView(videoSurface)

        //0 here is the user agora UID,but its an Int
        rtcEngine.setupLocalVideo(VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT,0 ))
    }

    private fun setupRemoteVideoFeed(UID:Int){
        val videoSurface:SurfaceView = RtcEngine.CreateRendererView(baseContext)
        videoSurface.setZOrderMediaOverlay(true)

        binding.flMainVideoContainer.addView(videoSurface)

        rtcEngine.setupRemoteVideo(VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, UID))
        rtcEngine.setRemoteSubscribeFallbackOption(io.agora.rtc.Constants.STREAM_FALLBACK_OPTION_AUDIO_ONLY)
    }

    private fun joinChannel(){
        rtcEngine.joinChannel(
            VIDEO_CHANNEL_1_TEMP_TOKEN,
            VIDEO_CALL_CHANNEL,
            "",
            generateNewVideoCallUserID())

        setupLocalVideoFeed()

        viewModel.onVideoCallStarted(callHistory)
    }

    private fun leaveChannel(){
        viewModel.onVideoCallStopped()

        rtcEngine.leaveChannel()
        removeVideo(binding.flSmallVideoContainer)
        removeVideo(binding.flMainVideoContainer)
        //assign appropriate visibility to other elements
    }
    private fun onRemoteUserLeft(){
       removeVideo(binding.flMainVideoContainer)
    }
    private fun removeVideo(container:FrameLayout){
        container.removeAllViews()
    }
    private fun onRemoteUserVideoToggle(UID:Int, state:Int){
        val videoSurface:SurfaceView = binding.flMainVideoContainer.getChildAt(0) as SurfaceView
        videoSurface.visibility = if(state == 0) View.GONE else View.VISIBLE

        //add an icon to let other user know that remote video has been disabled
        if(state == 0){
            val ivNoCamera: ImageView = ImageView(this)
            ivNoCamera.setImageResource(app.slyworks.base_feature.R.drawable.ic_videocam_off)

            binding.flMainVideoContainer.addView(ivNoCamera)
        }else{
            val ivNoCamera: ImageView? = binding.flMainVideoContainer.getChildAt(1) as ImageView
            if(ivNoCamera != null )
                binding.flMainVideoContainer.removeView(ivNoCamera)
        }
    }

    private fun toggleMuteStatus(status:Boolean){
        rtcEngine.muteLocalAudioStream(status)
        if(status){
            binding.fabToggleMute.isSelected = true
            binding.fabToggleMute.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, app.slyworks.base_feature.R.color.appBlack_semi)))
        }else{
            binding.fabToggleMute.isSelected = false
            binding.fabToggleMute.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)))
        }
    }

    private fun toggleVideoStatus(status:Boolean){
        rtcEngine.muteLocalVideoStream(status)
        binding.flSmallVideoContainer.isVisible = status

        val videoSurface:SurfaceView = binding.flSmallVideoContainer.getChildAt(0) as SurfaceView
        with(videoSurface){
            setZOrderMediaOverlay(status)
            isVisible = status
        }

        if(status){
            binding.fabSwitchVideo.isSelected = true
            binding.fabSwitchVideo.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, app.slyworks.base_feature.R.color.appBlack_semi)))
        }else{
            binding.fabSwitchVideo.isSelected = false
            binding.fabSwitchVideo.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)))
        }
    }


    override fun onBackPressed() = super.onBackPressed()
}