package app.slyworks.core_feature

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentTransaction
import app.slyworks.core_feature.databinding.FragmentProfileHostBinding
import app.slyworks.core_feature.main.MainActivity
import app.slyworks.core_feature.main.activityComponent
import app.slyworks.navigation_feature.Navigator
import app.slyworks.utils_lib.utils.displayMessage
import javax.inject.Inject

class ProfileHostFragment : Fragment() {

    private lateinit var binding:FragmentProfileHostBinding

    @Inject
    lateinit var viewModel:ProfileHostFragmentViewModel

    companion object {
        @JvmStatic
        fun getInstance(): ProfileHostFragment = ProfileHostFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        context.activityComponent
            .fragmentComponentBuilder()
            .setFragment(this)
            .build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        inflateFragment2(FindDoctorsFragment.getInstance())
    }

    private fun initData(){
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (childFragmentManager.backStackEntryCount > 1)
                            childFragmentManager.popBackStack()
                        else {
                            /*let the ParentActivity handle it*/
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    }
                })

        viewModel.progressLD.observe(viewLifecycleOwner){
            (requireActivity() as MainActivity).toggleProgressVisibility()
        }

        viewModel.messageLD.observe(viewLifecycleOwner){
            displayMessage(it, binding.root)
        }
    }

    fun inflateFragment2(f:Fragment){
        Navigator.transactionFrom(childFragmentManager)
            .into(R.id.fragment_container_profile_host)
            .hideCurrent()
            .show(f)
            .navigate()
    }

    fun inflateFragment3(f:Fragment){
        val transaction:FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        /*since there are only 2 fragments for this Fragment, if backStackEntryCount > 0,
        hide the first, which should always be FindDoctorFragment*/
        if(childFragmentManager.backStackEntryCount > 0)
            transaction.hide(childFragmentManager.fragments.first())

        transaction.addToBackStack("${f::class.simpleName}")
        transaction.add(R.id.fragment_container_profile_host, f, "${f::class.simpleName}")

        transaction.commit()
    }



}