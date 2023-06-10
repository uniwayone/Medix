package app.slyworks.auth_feature.verification

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.R
import app.slyworks.auth_feature.databinding.FragmentRegistrationGeneral0Binding
import app.slyworks.auth_feature.databinding.FragmentVerificationGeneral0Binding
import app.slyworks.auth_feature.registration.RegistrationActivity
import app.slyworks.auth_feature.registration.RegistrationActivityViewModel
import app.slyworks.auth_feature.registration.RegistrationGeneral1Fragment
import app.slyworks.auth_feature.registration.RegistrationOTP1Fragment
import app.slyworks.auth_lib.VerificationDetails
import app.slyworks.models_commons_lib.models.AccountType
import app.slyworks.utils_lib.utils.displayMessage
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class VerificationGeneral0Fragment : Fragment() {
    //region Vars
    private lateinit var viewModel:RegistrationActivityViewModel

    private val disposables: CompositeDisposable = CompositeDisposable()
    private lateinit var binding: FragmentVerificationGeneral0Binding
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): VerificationGeneral0Fragment =
            VerificationGeneral0Fragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = (requireActivity() as IRegViewModel).viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVerificationGeneral0Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
    }

    private fun initData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner){
            (requireActivity() as IRegViewModel).toggleProgressView(it)
        }

        viewModel.messageLiveData.observe(viewLifecycleOwner){
            displayMessage(it, binding.root)
        }
    }

    private fun initViews() {
        var selected: VerificationDetails = VerificationDetails.EMAIL

        disposables +=
        binding.sivEmail.observeChanges()
            .subscribe {
                if (it) {
                    binding.sivOtp.setCurrentStatus(false)
                    selected = VerificationDetails.EMAIL
                }
            }

        disposables +=
        binding.sivOtp.observeChanges()
            .subscribe {
                if (it) {
                    binding.sivEmail.setCurrentStatus(false)
                    selected = VerificationDetails.OTP
                }
            }

        disposables +=
        Observable.combineLatest(binding.sivEmail.observeChanges(),
            binding.sivOtp.observeChanges(),
            { isEmail: Boolean, isOTP: Boolean ->
                return@combineLatest isEmail || isOTP
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(binding.btnProceed::setEnabled)

        binding.btnProceed.setOnClickListener {
            when(selected){
                VerificationDetails.EMAIL -> viewModel.verifyByEmail()
                VerificationDetails.OTP -> {
                    (requireActivity() as VerificationActivity).navigator
                        .hideCurrent()
                        .show(RegistrationOTP1Fragment.newInstance())
                        .navigate()
                }
            }
        }
    }
}

