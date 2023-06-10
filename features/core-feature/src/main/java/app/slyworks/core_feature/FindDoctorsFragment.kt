package app.slyworks.core_feature

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.slyworks.constants_lib.EVENT_GET_DOCTOR_USERS
import app.slyworks.constants_lib.EVENT_OPEN_VIEW_PROFILE_FRAGMENT
import app.slyworks.controller_lib.AppController
import app.slyworks.controller_lib.Subscription
import app.slyworks.controller_lib.clearAndRemove
import app.slyworks.core_feature.databinding.FragmentFindDoctorsBinding
import app.slyworks.core_feature.view_profile.ViewProfileFragment
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.utils_lib.utils.addMultiple
import app.slyworks.utils_lib.utils.displayMessage

class FindDoctorsFragment : Fragment(), app.slyworks.controller_lib.Observer {
   //region Vars
    private val subscriptionsList:MutableList<Subscription> = mutableListOf()

    private lateinit var adapter: RVFindDoctorsAdapter
    private lateinit var binding:FragmentFindDoctorsBinding

    private lateinit var viewModel: ProfileHostFragmentViewModel
    //endregion

   companion object {
       @JvmStatic
       fun getInstance(): FindDoctorsFragment = FindDoctorsFragment()
   }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = (parentFragment as ProfileHostFragment).viewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptionsList.forEach { it.clearAndRemove() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFindDoctorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
    }

    private fun initData(){
        val subscription1 = AppController.subscribeTo(EVENT_GET_DOCTOR_USERS, this)
        val subscription2: Subscription = AppController.subscribeTo(EVENT_OPEN_VIEW_PROFILE_FRAGMENT, this)

        subscriptionsList.addMultiple(subscription1, subscription2)

        viewModel.messageLD.observe(viewLifecycleOwner){
           displayMessage(it, binding.root)
        }

        viewModel.progressLD.observe(viewLifecycleOwner){
            (requireParentFragment() as ProfileHostFragment).toggleProgressVisibility()
        }

        viewModel.doctorsListLiveData.observe(viewLifecycleOwner, Observer {
            binding.layoutIntroFragFindDoctors.visibility = View.GONE
            binding.rootView.visibility = View.VISIBLE
            adapter.setDataList(it)
        })

    }

    private fun initViews(){
        binding.btnFindDoctorsFragFindDoctors.setOnClickListener {
            viewModel.getAllDoctors()
        }

        adapter = RVFindDoctorsAdapter()

        binding.rvFindDoctorsFindDoctors.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFindDoctorsFindDoctors.addItemDecoration(DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL))
        binding.rvFindDoctorsFindDoctors.adapter = adapter
    }

    override fun <T> notify(event: String, data: T?) {
        when(event){
            EVENT_OPEN_VIEW_PROFILE_FRAGMENT ->{
                val entity: FBUserDetailsVModel = data as FBUserDetailsVModel

                (requireParentFragment() as ProfileHostFragment)
                    .inflateFragment2(ViewProfileFragment.newInstance(entity))
            }

        }
    }
}