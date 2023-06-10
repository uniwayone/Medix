package app.slyworks.auth_feature.registration

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.databinding.FragmentRegistrationGeneral0Binding
import app.slyworks.constants_lib.ACCOUNT_TYPE_NOT_SET
import app.slyworks.constants_lib.DOCTOR
import app.slyworks.constants_lib.NOT_SET
import app.slyworks.constants_lib.PATIENT
import app.slyworks.models_commons_lib.models.AccountType
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RegistrationGeneral0Fragment : Fragment() {
    //region Vars
    private lateinit var binding: FragmentRegistrationGeneral0Binding
    private lateinit var viewModel: RegistrationActivityViewModel
     private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): RegistrationGeneral0Fragment = RegistrationGeneral0Fragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = (context as IRegViewModel).viewModel
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       binding = FragmentRegistrationGeneral0Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews(){
        // val animationLogo = AnimationUtils.loadAnimation(this, R.anim.registration_logo_anim)

      /*  binding.ivLogo.alpha = 0F
        val logoAnimator: ValueAnimator = ValueAnimator.ofFloat(0f,1f)
        logoAnimator.duration = 1_500
        logoAnimator.interpolator = LinearInterpolator()
        logoAnimator.addUpdateListener {
            val animatorValue:Float = it.animatedValue as Float

            binding.ivLogo.alpha = animatorValue
            binding.ivLogo.scaleX = animatorValue
            binding.ivLogo.scaleY = animatorValue
        }
        logoAnimator.start()

        val animationText = AnimationUtils.loadAnimation(requireContext(), app.slyworks.base_feature.R.anim.regisrtration_text_anim)
        animationText.startOffset = 500

        binding.tvText.startAnimation(animationText)*/

        disposables +=
        binding.sivPatient.observeChanges()
            .subscribe {
                if(it){
                   binding.sivDoctor.setCurrentStatus(false)
                   viewModel.registrationDetails.accountType = AccountType.PATIENT
                }
            }

        disposables +=
        binding.sivDoctor.observeChanges()
            .subscribe {
                if(it){
                    binding.sivPatient.setCurrentStatus(false)
                    viewModel.registrationDetails.accountType = AccountType.DOCTOR
                }
            }

        disposables +=
        Observable.combineLatest(binding.sivPatient.observeChanges(),
                                 binding.sivDoctor.observeChanges(),
            { isPatient:Boolean, isDoctor:Boolean ->
                return@combineLatest isPatient || isDoctor
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(binding.btnNext::setEnabled)

        binding.btnNext.setOnClickListener {
            (requireActivity() as RegistrationActivity)
                .navigator
                .hideCurrent()
                .show(RegistrationGeneral1Fragment.newInstance())
                .navigate()
        }
    }
}