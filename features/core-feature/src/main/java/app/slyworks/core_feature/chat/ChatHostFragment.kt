package app.slyworks.core_feature.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import app.slyworks.core_feature.main.MainActivity
import app.slyworks.core_feature.R
import app.slyworks.core_feature.VPAdapter
import app.slyworks.core_feature.main.activityComponent
import app.slyworks.utils_lib.utils.displayImage
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject


class ChatHostFragment : Fragment() {
    //region Vars
    private lateinit var layout_vp:CoordinatorLayout
    private lateinit var ivToggle:CircleImageView
    private lateinit var ivProfile:CircleImageView
    private lateinit var vpChatHostFragment: ViewPager2
    private lateinit var tabLayoutChatHostFragment:TabLayout

    private lateinit var containerSecondary:FragmentContainerView

    private lateinit var vpAdapter: VPAdapter

    lateinit var viewModel: ChatHostFragmentViewModel
    //endregion

    companion object {
        val tabTitles:MutableList<String> = mutableListOf("Chats", "Call History")

        @JvmStatic
        fun getInstance(): ChatHostFragment = ChatHostFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        context.activityComponent
            .fragmentComponentBuilder()
            .setFragment(this)
            .build()
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_chat_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view:View){
        layout_vp = view.findViewById(R.id.layout_vp_frag_chat_host)
        ivToggle = view.findViewById(R.id.ivToggle_collapsing_toolbar)
        ivProfile = view.findViewById(R.id.ivProfile_collapsing_toolbar)
        vpChatHostFragment = view.findViewById(R.id.vpHost_frag_chat_host)
        tabLayoutChatHostFragment = view.findViewById(R.id.tabLayout_frag_chat_host)

        containerSecondary = view.findViewById(R.id.fragment_container_chat_host)

        val lifecycle:Lifecycle = this.lifecycle
        vpAdapter = VPAdapter(childFragmentManager, lifecycle)
        vpChatHostFragment.adapter = vpAdapter
        TabLayoutMediator(tabLayoutChatHostFragment, vpChatHostFragment,
            object: TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    tab.setText(tabTitles[position])
                }
            }).attach()

        ivToggle.setOnClickListener { (requireActivity() as MainActivity).toggleDrawerState() }

        ivProfile.displayImage(viewModel.getUserDetailsUser().imageUri)
    }

    fun inflateFragment(f:Fragment){
        val transaction = childFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        if(f.isAdded) transaction.show(f)
        else transaction.replace(R.id.fragment_container_chat_host, f, "${f::class.simpleName}")

        transaction.commit()

        layout_vp.visibility = View.GONE
    }
}