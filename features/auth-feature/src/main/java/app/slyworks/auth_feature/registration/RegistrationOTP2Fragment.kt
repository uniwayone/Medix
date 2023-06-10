package app.slyworks.auth_feature.registration

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.databinding.FragmentRegistrationOtp2Binding
import app.slyworks.constants_lib.MAIN_ACTIVITY_INTENT_FILTER
import app.slyworks.navigation_feature.Navigator
import app.slyworks.utils_lib.utils.closeKeyboard3
import app.slyworks.utils_lib.utils.plusAssign
import app.slyworks.utils_lib.utils.displayMessage
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RegistrationOTP2Fragment : Fragment() {
    //region Vars
    private lateinit var binding: FragmentRegistrationOtp2Binding
    private lateinit var viewModel: RegistrationActivityViewModel

    private val disposables = CompositeDisposable()
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): RegistrationOTP2Fragment = RegistrationOTP2Fragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = (requireActivity() as IRegViewModel).viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrationOtp2Binding.inflate(layoutInflater, container, false)
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
        viewModel.verificationFailureLiveData.observe(viewLifecycleOwner){}

        viewModel.verificationSuccessfulLiveData.observe(viewLifecycleOwner){
            Navigator.intentFor(requireContext(), MAIN_ACTIVITY_INTENT_FILTER)
                .newAndClearTask()
                .navigate()
        }

        viewModel.otpCountDownLD.observe(viewLifecycleOwner){
            binding.tvCounter.setText(it.toString())
        }

        viewModel.otpCountDownFinishedLD.observe(viewLifecycleOwner){
            binding.tvResendOtp.isEnabled = it
        }

        viewModel.initOTPTimeoutCountdown()
    }

    private fun initViews(){
        disposables +=
        binding.etOTP1.textChanges()
            .subscribe {
               if(it.length == 1)
                   binding.etOTP2.requestFocus()
            }
        disposables +=
        binding.etOTP2.textChanges()
            .subscribe {
               if(it.length == 1)
                   binding.etOTP3.requestFocus()
            }

        disposables +=
        binding.etOTP3.textChanges()
            .subscribe {
               if(it.length == 1)
                   binding.etOTP4.requestFocus()
            }

        disposables +=
        binding.etOTP4.textChanges()
            .subscribe {
               if(it.length == 1)
                   binding.etOTP5.requestFocus()
            }

        disposables +=
        binding.etOTP5.textChanges()
            .subscribe {
               if(it.length == 1)
                   binding.etOTP6.requestFocus()
            }

        binding.etOTP6.setOnEditorActionListener(
            TextView.OnEditorActionListener { p0, p1, p2 ->
                if(p0!!.id == binding.etOTP6.id){
                    requireActivity().closeKeyboard3()

                    val otp:String =
                        "${binding.etOTP1.text}${binding.etOTP2.text}" +
                        "${binding.etOTP3.text}${binding.etOTP4.text}"
                    viewModel.inputOTPSubject.onNext(otp)
                    return@OnEditorActionListener true
                }

                return@OnEditorActionListener false
            })

        disposables +=
        Observable.combineLatest(binding.etOTP1.textChanges(),
                                 binding.etOTP2.textChanges(),
                                 binding.etOTP3.textChanges(),
                                 binding.etOTP4.textChanges(),
                                 binding.etOTP5.textChanges(),
                                 binding.etOTP6.textChanges(),
            { t1,t2,t3,t4,t5,t6 ->
                t1.length == 1 && t2.length == 1 && t3.length == 1 && t4.length == 1 && t5.length == 1 && t6.length == 1
            })
            .subscribe(binding.btnNext::setEnabled)

        binding.tvResendOtp.setOnClickListener{
           viewModel.resendOTPSubject.onNext(true)
        }

        binding.btnNext.setOnClickListener {
            requireActivity().closeKeyboard3()

            val otp:String =
            "${binding.etOTP1.text}${binding.etOTP2.text}" +
            "${binding.etOTP3.text}${binding.etOTP4.text}"
            viewModel.inputOTPSubject.onNext(otp)
        }


    }
}