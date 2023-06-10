package app.slyworks.message_feature.message

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.base_feature.BaseActivity
import app.slyworks.base_feature.MOnBackPressedCallback
import app.slyworks.constants_lib.*
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.message_feature.R
import app.slyworks.message_feature.custom_views.SpacingItemDecorator
import app.slyworks.navigation_feature.Navigator
import app.slyworks.navigation_feature.Navigator.Companion.getParcelable
import app.slyworks.utils_lib.IDHelper.Companion.generateNewMessageID
import app.slyworks.utils_lib.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding4.widget.textChanges
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class MessageActivity : BaseActivity() {
    //region Vars
    private lateinit var rootView:ConstraintLayout

    private lateinit var ivBack:ImageView
    private lateinit var ivProfile: CircleImageView
    private lateinit var tvName:TextView
    private lateinit var tvConnectionStatus:TextView
    private lateinit var ivVideoCall:ImageView
    private lateinit var ivVoiceCall:ImageView
    private lateinit var ivMore:ImageView

    private lateinit var rvMessages:RecyclerView
    private lateinit var fabScrollUp:FloatingActionButton
    private lateinit var fabScrollDown:FloatingActionButton

    private lateinit var ivEmoji:ImageView
    private lateinit var ivAttachment:ImageView
    private lateinit var etMessage:EditText
    private lateinit var fabSend:FloatingActionButton
    private lateinit var fabVoiceNote:FloatingActionButton

    private lateinit var progress:ProgressBar

    private val disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var adapter: RVMessageAdapter

    private lateinit var userProfile: FBUserDetailsVModel

    @Inject
    lateinit var viewModel: MessageActivityViewModel
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_message)

        initViews()
        initData()
    }

    private fun initDI(){
        /*application.appComponent
            .activityComponentBuilder()
            .setActivity(this)
            .build()
            .inject(this)*/
    }

    private fun setUserData(userDetails: FBUserDetailsVModel){
        ivProfile.displayImage(userDetails.imageUri)

        val name =
            if(userDetails.accountType == "DOCTOR")
                "Dr. ${userDetails.fullName}"
            else
                userDetails.fullName
        tvName.text = name
    }

    private fun initData(){
        //fixme:could be a FBUserDetails object with some values missing if it comes from ChatFragment and not ViewProfileActivity
        userProfile = intent.getParcelable<FBUserDetailsVModel>(EXTRA_USER_PROFILE_FBU)!!
        setUserData(userProfile)

        this.onBackPressedDispatcher
            .addCallback(this, MOnBackPressedCallback(this))

        viewModel.observeConnectionStatus(userProfile.firebaseUID)
                  .observe(this){
                      if (it != "online")
                          tvConnectionStatus.setTextColor(
                              ContextCompat.getColor(this, app.slyworks.base_feature.R.color.appGrey_li_message_from))
                      else
                          tvConnectionStatus.setTextColor(
                              ContextCompat.getColor(this, app.slyworks.base_feature.R.color.appGreen_text))

                      tvConnectionStatus.setText(it)
        }

        viewModel.observeMessagesForUID(userProfile.firebaseUID).observe(this) {
            adapter.submitList(it)
        }

        viewModel.progressLiveData.observe(this){
            progress.isVisible = it
            rootView.setChildViewsStatus(it)
        }

        viewModel.statusLiveData.observe(this){
            showMessage(it,rootView)
        }
        viewModel.startCallStateLiveData
            .observe(this){
                if(it){
                    userProfile = viewModel.startCallDataLiveData.value!!
                    startCall()
                }
            }

        /*clearing the user's unread message count*/
        viewModel.updatePersonLastMessageInfo(userProfile.firebaseUID)
    }

    private fun initViews(){
        rootView = findViewById(R.id.rootView)
        ivBack = findViewById(R.id.ivback_frag_message)
        ivProfile = findViewById(R.id.ivProfile_frag_message)
        tvName = findViewById(R.id.tvName_frag_message)
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus_frag_message)

        ivVoiceCall = findViewById(R.id.ivVoiceCall_frag_message)
        ivVideoCall = findViewById(R.id.ivVideoCall_frag_message)
        ivMore = findViewById(R.id.ivMore_frag_message)

        rvMessages = findViewById(R.id.rvMessages_frag_message)

        fabScrollUp = findViewById(R.id.fab_scroll_up_frag_message)
        fabScrollDown = findViewById(R.id.fab_scroll_down_frag_message)

        ivEmoji = findViewById(R.id.ivEmoji)
        ivAttachment = findViewById(R.id.ivAttachment)
        etMessage = findViewById(R.id.etMessage_message)
        fabSend = findViewById(R.id.fab_send_layout_message)
        fabVoiceNote = findViewById(R.id.fab_record_layout_message)

        progress = findViewById(R.id.progress_layout)

        progress.visibility = View.VISIBLE

        ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = RVMessageAdapter(rvMessages, viewModel.timeHelper)
        rvMessages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMessages.addItemDecoration(SpacingItemDecorator())
        //rvMessages.addItemDecoration(EdgeItemDecorator())
        //rvMessages.addItemDecoration(StickyHeaderItemDecorator())
        rvMessages.adapter = adapter

        disposables +=
            etMessage.textChanges()
                .subscribe { toggleFabSendVisibility(it.isNotEmpty()) }

        //implement in the adapter
        fabScrollUp.setOnClickListener { adapter.scrollToTop() }
        fabScrollDown.setOnClickListener { adapter.scrollToBottom() }

        ivVideoCall.setOnClickListener { startCall() }

        fabSend.setOnClickListener {
            val message: MessageVModel = MessageVModel(
                type = OUTGOING_MESSAGE,
                fromUID = viewModel.getUserDetailsUtils().firebaseUID,
                toUID = userProfile.firebaseUID,
                senderFullName = viewModel.getUserDetailsUtils().fullName,
                receiverFullName = userProfile.fullName,
                content = etMessage.text.toString().trim(),
                timeStamp = System.currentTimeMillis().toString(),
                messageID = generateNewMessageID(),
                status = NOT_SENT,
                senderImageUri = viewModel.getUserDetailsUtils().imageUri,
                accountType = viewModel.getUserDetailsUtils().accountType,
                FCMRegistrationToken = viewModel.getUserDetailsUtils().FCMRegistrationToken,
                receiverImageUri = userProfile.imageUri )

            viewModel.sendMessage(message)

            etMessage.getText().clear()
            closeKeyboard3()
        }
    }

    private fun startCall(){
        /*checking for a property to ensure its the complete FBUserDetails object at this point*/
        if(userProfile.firstName.isNullOrEmpty()){
            showMessage("setting up your call, please wait", rootView)
            viewModel.getUserDetails(userProfile.firebaseUID)
            return
        }

        /*fixme:VideoCallActivity is expecting a Bundle, fix that*/
        Navigator.intentFor(this, VIDEOCALL_ACTIVITY_INTENT_FILTER)
            .addExtra(EXTRA_VIDEO_CALL_TYPE, VIDEO_CALL_OUTGOING)
            .addExtra(EXTRA_VIDEO_CALL_USER_DETAILS, userProfile)
            .navigate()
    }

    private fun toggleFabSendVisibility(status:Boolean){
        fabSend.isVisible = status
        fabVoiceNote.isVisible = !status
    }



}