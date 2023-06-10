package app.slyworks.core_feature.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.base_feature.custom_views.HorizontalSpacingItemDecorator
import app.slyworks.core_feature.main.MainActivity
import app.slyworks.core_feature.R
import app.slyworks.core_feature.RvHealthAreasAdapter
import app.slyworks.core_feature.main.activityComponent
import app.slyworks.models_commons_lib.models.AccountType
import app.slyworks.utils_lib.utils.displayImage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import javax.inject.Inject

class DoctorHomeFragment : Fragment() {
    //region Vars
    private lateinit var ivToggle: ImageView
    private lateinit var ivProfile:CircleImageView
    private lateinit var tvUserName:TextView
    private lateinit var ivNotification:CircleImageView
    private lateinit var layout_notification_count: ConstraintLayout
    private lateinit var tvNotificationCount:TextView

    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var actionMessages: ConstraintLayout
    private lateinit var actionConsultations: ConstraintLayout
    private lateinit var actionHealthCareCenters: ConstraintLayout
    private lateinit var actionReminder: ConstraintLayout
    private lateinit var actionVoiceCalls: ConstraintLayout
    private lateinit var actionVideoCalls: ConstraintLayout

    private lateinit var rvHealthAreas:RecyclerView

    private lateinit var tvUpcomingConsultation:TextView

    private lateinit var cardSchedule: ConstraintLayout
    private lateinit var ivProfile_cardSchedule:CircleImageView
    private lateinit var tvName_cardSchedule:TextView
    private lateinit var tvDetails_cardSchedule:TextView
    private lateinit var tvDate_cardSchedule:TextView
    private lateinit var tvTime_cardSchedule:TextView

    private lateinit var type: AccountType

    private lateinit var adapterHealthAreas: RvHealthAreasAdapter

    private lateinit var parentActivity: MainActivity

    @Inject
    lateinit var viewModel: HomeFragmentViewModel
    //endregion

    companion object {
        @JvmStatic
        fun getInstance(): DoctorHomeFragment = DoctorHomeFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as MainActivity

         context.activityComponent
             .fragmentComponentBuilder()
             .setFragment(this)
             .build()
             .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home_doctor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews(view)
    }

    private fun initData(){
        viewModel.imageUriLiveData.observe(viewLifecycleOwner){
            ivProfile.displayImage(it)
        }

        viewModel.observeUserProfilePic()
    }

    private fun initViews(view:View){
        ivToggle = view.findViewById(R.id.ivToggle_collapsing_toolbar)
        ivNotification = view.findViewById(R.id.ivNotifications_collapsing_toolbar)
        ivProfile = view.findViewById(R.id.ivProfile_collapsing_toolbar)
        tvUserName = view.findViewById(R.id.tvUsername_collapsing_toolbar)

        layout_notification_count = view.findViewById(R.id.layout_unread_message_count)
        tvNotificationCount = view.findViewById(R.id.tvUnreadMessageCount)

        tvUpcomingConsultation = view.findViewById(R.id.tvSeeAll_upcomingCons_frag_home)

        searchView = view.findViewById(R.id.searchView_frag_home)

        actionMessages = view.findViewById(R.id.quick_action_message)
        actionConsultations = view.findViewById(R.id.quick_action_consultations)
        actionHealthCareCenters = view.findViewById(R.id.quick_action_healthcare_centers)
        actionReminder = view.findViewById(R.id.quick_action_reminder)
        actionVoiceCalls = view.findViewById(R.id.quick_action_voicecalls)
        actionVideoCalls = view.findViewById(R.id.quick_action_videocalls)

        cardSchedule = view.findViewById(R.id.layout_schedule_card)
        ivProfile_cardSchedule = view.findViewById(R.id.ivProfile_layout_schedule_frag_home)
        tvName_cardSchedule = view.findViewById(R.id.tvName_layout_schedule_frag_home)
        tvDetails_cardSchedule = view.findViewById(R.id.tvProfileDetails_layout_schedule_frag_home)
        tvDate_cardSchedule = view.findViewById(R.id.tvDate_layout_schedule_frag_home)
        tvTime_cardSchedule = view.findViewById(R.id.tvTime_layout_schedule_frag_home)

        tvName_cardSchedule.setText("Josh Sylvanus")
        tvDetails_cardSchedule.setText("Abuja, Nigeria")

        tvNotificationCount.setText(1.toString())

        ivProfile.displayImage(viewModel.getUser().imageUri)

        ivProfile_cardSchedule.displayImage(viewModel.getUser().imageUri)

        ivToggle.setOnClickListener{ parentActivity.toggleDrawerState() }

        val name: String = "Dr. " + viewModel.getUser().firstName
            .substring(0, 1)
            .uppercase(Locale.getDefault())
            .plus(
                viewModel.getUser().firstName
                .substring(1, viewModel.getUser().firstName.length))
        tvUserName.text = name

        adapterHealthAreas = RvHealthAreasAdapter()
        rvHealthAreas = view.findViewById(R.id.rvHealthAreas_frag_home)
        rvHealthAreas.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        rvHealthAreas.addItemDecoration(HorizontalSpacingItemDecorator())
        rvHealthAreas.adapter = adapterHealthAreas
    }


}