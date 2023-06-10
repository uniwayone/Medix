package app.slyworks.base_feature.custom_views

import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.hdodenhof.circleimageview.CircleImageView


/**
 *Created by Joshua Sylvanus, 7:58 AM, 11/05/2022.
 */
class IncomingVideoCallView {
    //region Vars
    private lateinit var layout_new_message: ConstraintLayout
    private lateinit var layout_incoming_video_call: ConstraintLayout
    private lateinit var ivProfileVideoCall: CircleImageView
    private lateinit var tvMessageVideoCall: TextView
    private lateinit var btnAcceptVideoCall: Button
    private lateinit var btnDeclineVideoCall: Button
    //endregion

    /*
    *
    *  fun navigateToVideoCall(){
            val intent:Intent = Intent(this, VideoCallActivity::class.java)
            val b:Bundle = Bundle().apply {
                putString(EXTRA_VIDEO_CALL_TYPE, VIDEO_CALL_INCOMING)
                putParcelable(EXTRA_VIDEO_CALL_USER_DETAILS, mVideoCallUserDetails)
            }
            NavigationManager2.inflateActivity(
                this,
                ActivityWrapper.VIDEO_CALL,
                false,
                isToBeFinished = true,
                b)
        }

        layout_new_message = findViewById(R.id.layout_message_alert)
        layout_incoming_video_call = findViewById(R.id.layout_invite)
        ivProfileVideoCall = findViewById(R.id.ivProfile_video_call_invite)
        tvMessageVideoCall = findViewById(R.id.tvDetails_video_call_invite)
        btnAcceptVideoCall = findViewById(R.id.btnAccept_video_invite)
        btnDeclineVideoCall = findViewById(R.id.btnDecline_video_invite)

      layout_incoming_video_call.setOnClickListener {
            navigateToVideoCall()
        }

        btnAcceptVideoCall.setOnClickListener {
            navigateToVideoCall()
        }

        btnDeclineVideoCall.setOnClickListener {
            layout_incoming_video_call.visibility = View.GONE
            CoroutineScope(Dispatchers.IO).launch {
                CallManager.processVideoCall(
                    type = VIDEO_CALL_INCOMING,
                    firebaseUID = mVideoCallUserDetails!!.firebaseUID,
                    REQUEST_DECLINED)
            }
        }
        *
        *  layout_new_message.setOnClickListener {
            layout_new_message.visibility = View.GONE
            inflateFragment(ChatHostFragment.newInstance())
            updateActiveItem(mFragmentMap[FragmentWrapper.CHAT_HOST]!!)
        }

        * */
}