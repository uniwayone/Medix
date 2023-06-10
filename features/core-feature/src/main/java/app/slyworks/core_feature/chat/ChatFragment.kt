package app.slyworks.core_feature.chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.constants_lib.*
import app.slyworks.controller_lib.AppController
import app.slyworks.controller_lib.Observer
import app.slyworks.controller_lib.Subscription
import app.slyworks.controller_lib.clearAndRemove
import app.slyworks.core_feature.main.MainActivity
import app.slyworks.core_feature.R
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.data_lib.models.MessageVModel
import app.slyworks.data_lib.models.PersonVModel
import app.slyworks.navigation_feature.Navigator
import app.slyworks.utils_lib.utils.addMultiple
import com.google.android.material.floatingactionbutton.FloatingActionButton

import javax.inject.Inject

class ChatFragment : Fragment(), Observer {
    //region Vars
    private lateinit var layout_chat_empty:ConstraintLayout
    private lateinit var rvChats:RecyclerView
    private lateinit var fabStartChat: FloatingActionButton
    private lateinit var rootView:CoordinatorLayout
    private lateinit var progress:ProgressBar
    private lateinit var layout_error:ConstraintLayout
    private lateinit var tvRetry:TextView
    private lateinit var btnRetry:Button
    private lateinit var progress_retry:ProgressBar

    private lateinit var personToMessagesMap:Map<PersonVModel, MutableList<MessageVModel>>
    private lateinit var adapter2: RVChatAdapter2

    private lateinit var viewModel: ChatHostFragmentViewModel

    private var subscriptionsList:MutableList<Subscription> = mutableListOf()

    //endregion
    companion object {
        @JvmStatic
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = (parentFragment as ChatHostFragment).viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        initViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
    }

    private fun initViews(view:View){
        layout_chat_empty = view.findViewById(R.id.layout_no_messages_content_chat)
        rvChats = view.findViewById(R.id.rvChats_frag_chat)
        fabStartChat = view.findViewById(R.id.fabStatChat_frag_chat)
        rootView = view.findViewById(R.id.rootView)
        progress = view.findViewById(R.id.progress_layout)
        layout_error = view.findViewById(R.id.layout_error)
        tvRetry = view.findViewById(R.id.tvRetry_content_chat)
        btnRetry = view.findViewById(R.id.btnRetry_content_chat)
        progress_retry = view.findViewById(R.id.progrss_retry)

        fabStartChat.setOnClickListener {
            val f:String
            if(viewModel.getUserAccountType() == PATIENT)
                f = FRAGMENT_PROFILE_HOST
            else
                f = FRAGMENT_DOCTOR_HOME

            (requireActivity() as MainActivity)
                .inflateFragment(f)
        }

        adapter2 = RVChatAdapter2()
        rvChats.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvChats.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        rvChats.adapter = adapter2

        btnRetry.setOnClickListener { getData(1) }
    }

    private fun initData(){
        val subscription = AppController.subscribeTo(EVENT_OPEN_MESSAGE_ACTIVITY, this)
        val subscription2 = AppController.subscribeTo(EVENT_OPEN_MESSAGE_ACTIVITY_2, this)

        subscriptionsList.addMultiple(subscription, subscription2)

        viewModel.successStateLiveData.observe(viewLifecycleOwner){
            personToMessagesMap = it
            adapter2.submitList(it.keys.toList())
        }

        viewModel.intoStateLiveData.observe(viewLifecycleOwner){
            toggleLayoutIntroStatus(it)
        }

        viewModel.errorStateLiveData.observe(viewLifecycleOwner){
            if(it){
              val text = viewModel.errorDataLiveData.value!!
              toggleLayoutErrorStatus(it, text)
              return@observe
            }

            toggleLayoutErrorStatus(it)
        }

        viewModel.progressStateLiveData.observe(viewLifecycleOwner){
            progress.isVisible = it
            if(!it && progress_retry.isVisible)
              progress_retry.visibility = View.GONE
        }

        getData(2)
    }

    private fun getData(from:Int){
        when(from){
            1 -> progress_retry.isVisible = true
            2 -> progress.isVisible = true
        }

        viewModel.getChats()
    }

    private fun toggleLayoutErrorStatus(status:Boolean,
                                        text:String = "oops, something went wrong on our end, please try again"){
        layout_error.isVisible = status
        tvRetry.text = text
    }

    private fun toggleLayoutIntroStatus(status:Boolean){
        layout_chat_empty.isVisible = status
    }

    override fun <T> notify(event: String, data: T?) {
        when(event){
            EVENT_OPEN_MESSAGE_ACTIVITY ->{}
            EVENT_OPEN_MESSAGE_ACTIVITY_2 ->{
                /*TODO:this is an incomplete FBUserDetails object*/
                val result: PersonVModel = data as PersonVModel

                val entity: FBUserDetailsVModel = FBUserDetailsVModel(
                    accountType = result.userAccountType,
                    firebaseUID = result.firebaseUID,
                    fullName = result.fullName,
                    imageUri = result.imageUri)

                Navigator.intentFor(requireActivity(), MESSAGE_ACTIVITY_INTENT_FILTER)
                    .addExtra(EXTRA_USER_PROFILE_FBU, entity)
                    .navigate()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptionsList.forEach { it.clearAndRemove() }
    }
}