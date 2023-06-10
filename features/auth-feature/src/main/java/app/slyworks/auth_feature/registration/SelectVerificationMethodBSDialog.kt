package app.slyworks.auth_feature.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.auth_feature.databinding.BottomsheetSelectVerificationMethodBinding
import app.slyworks.auth_lib.VerificationDetails
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject


/**
 * Created by Joshua Sylvanus, 9:27 PM, 11/11/2022.
 */
class SelectVerificationMethodBSDialog : app.slyworks.base_feature.BaseBottomSheetDialogFragment() {
    //region Vars
    private lateinit var binding: BottomsheetSelectVerificationMethodBinding
    private val disposables:CompositeDisposable = CompositeDisposable()
    private var subject:PublishSubject<VerificationDetails> = PublishSubject.create()
    //endregion

    companion object{
        @JvmStatic
        fun getInstance(): SelectVerificationMethodBSDialog = SelectVerificationMethodBSDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomsheetSelectVerificationMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews(){
        var selected: VerificationDetails = VerificationDetails.EMAIL

        disposables +=
        binding.sivEmail.observeChanges()
            .subscribe{
                if(it){
                    binding.sivOtp.setCurrentStatus(false)
                    selected = VerificationDetails.EMAIL
                }
            }

        disposables +=
        binding.sivOtp.observeChanges()
            .subscribe{
                if(it){
                    binding.sivEmail.setCurrentStatus(false)
                    selected = VerificationDetails.OTP
                }
            }

        disposables +=
            Observable.combineLatest(binding.sivEmail.observeChanges(),
                                     binding.sivOtp.observeChanges(),
                { isEmail:Boolean, isOTP:Boolean ->
                    return@combineLatest isEmail || isOTP
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(binding.btnProceed::setEnabled)

        binding.btnProceed.setOnClickListener {
            subject.onNext(selected)
        }
    }

    fun getSubject():Observable<VerificationDetails> = subject.hide()
}