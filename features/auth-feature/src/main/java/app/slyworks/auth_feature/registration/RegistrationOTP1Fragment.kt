package app.slyworks.auth_feature.registration

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.databinding.FragmentRegistrationOtp1Binding
import app.slyworks.utils_lib.utils.closeKeyboard3
import app.slyworks.utils_lib.utils.displayMessage
import app.slyworks.utils_lib.utils.plusAssign
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RegistrationOTP1Fragment : Fragment() {
    //region Vars
    private lateinit var binding: FragmentRegistrationOtp1Binding
    private lateinit var viewModel: RegistrationActivityViewModel
    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): RegistrationOTP1Fragment = RegistrationOTP1Fragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = (context as IRegViewModel).viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrationOtp1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
    }

    private fun initData(){
        viewModel.progressLiveData.observe(viewLifecycleOwner){}
        viewModel.messageLiveData.observe(viewLifecycleOwner){ displayMessage(it, binding.root) }
        viewModel.beginOTPVerificationLiveData.observe(viewLifecycleOwner){_ ->
            (requireActivity() as IRegViewModel).navigator
                .hideCurrent()
                .show(RegistrationOTP2Fragment.newInstance())
                .navigate()
        }
    }

    private fun initViews(){
        disposables +=
        binding.etPhoneNumber.textChanges()
            .map{ it.length == 14 }
            .subscribe(binding.btnNext::setEnabled)

        val nextFunc:() -> Unit = {
            requireActivity().closeKeyboard3()

            val phoneNumber:String = binding.etPhoneNumber.text.toString().trim()
            viewModel.verifyViaOTP(phoneNumber, requireActivity())
        }

        binding.etPhoneNumber.setOnEditorActionListener(
            TextView.OnEditorActionListener{ p0, _, _ ->
                if(p0!!.id == binding.etPhoneNumber.id){
                    nextFunc()
                    return@OnEditorActionListener true
                }

                return@OnEditorActionListener false
            })

        binding.btnNext.setOnClickListener{ nextFunc() }
    }
}