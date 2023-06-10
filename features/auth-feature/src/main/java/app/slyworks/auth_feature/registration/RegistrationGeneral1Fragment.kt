package app.slyworks.auth_feature.registration

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.databinding.FragmentRegistrationGeneral1Binding
import app.slyworks.utils_lib.utils.plusAssign
import app.slyworks.utils_lib.utils.closeKeyboard3
import app.slyworks.utils_lib.utils.displayMessage
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.lang.reflect.Modifier

class RegistrationGeneral1Fragment : Fragment() {
    //region Vars
    private lateinit var binding: FragmentRegistrationGeneral1Binding
    private lateinit var viewModel: RegistrationActivityViewModel

    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): RegistrationGeneral1Fragment =
            RegistrationGeneral1Fragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = (context as RegistrationActivity).viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegistrationGeneral1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
    }

    private fun initData(){
       viewModel.progressLiveData.observe(viewLifecycleOwner){
           (requireActivity() as IRegViewModel).toggleProgressView(it)
       }
    }

    private fun initViews(){
        disposables +=
        Observable.combineLatest(
            binding.etEmail.textChanges(),
            binding.etPassword.textChanges(),
            binding.etConfirmPassword.textChanges(),
            { email, password, confirmPassword ->
               email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
            })
            .subscribe(binding.btnNext::setEnabled)

        binding.etConfirmPassword.setOnEditorActionListener(
            TextView.OnEditorActionListener { p0, p1, p2 ->
                if(p0!!.id == binding.etConfirmPassword.id){
                    requireActivity().closeKeyboard3()
                    processUserDetails()
                    return@OnEditorActionListener true
                }

                return@OnEditorActionListener false
            })

        binding.btnNext.setOnClickListener {
            requireActivity().closeKeyboard3()
            processUserDetails()
        }
    }

    @VisibleForTesting(otherwise = Modifier.PRIVATE)
    internal fun check(email:String, password:String, confirmPassword:String):Boolean{
        var result = true

        if(TextUtils.isEmpty(email)){
            displayMessage("please enter your email", binding.root)
            result = false
        } else if(TextUtils.isEmpty(password)){
            displayMessage("please enter your password", binding.root)
            result = false
        } else if(TextUtils.isEmpty(confirmPassword)){
            displayMessage("please repeat the entered password", binding.root)
            result = false
        }else if(!email.contains("@")){
            displayMessage("please enter a valid email address", binding.root)
            result = false
        }else if(password.length < 8){
            displayMessage("Password should be a minimum of 8 characters", binding.root)
            result = false
        }else if(!password.contains("[A-Z]".toRegex())){
            displayMessage("Password should contain at least 1 uppercase letter", binding.root)
            result = false
        }else if(!password.contains("[0-9]".toRegex())){
            displayMessage("Password should contain at least 1 number", binding.root)
            result = false
        }else if(!password.contains("[@#\$%^&+=.]".toRegex())){
            displayMessage("Password should contain at least 1 special character(&,%,#,@,$, e.t.c)", binding.root)
            result = false
        }else if(!TextUtils.equals(password, confirmPassword)){
            displayMessage("Passwords do not match, please check and try again", binding.root)
            result = false
        }

        return result
    }

    private fun processUserDetails(){
        val email:String = binding.etEmail.text.toString().trim()
        val password:String = binding.etPassword.text.toString().trim()
        val confirmPassword:String = binding.etConfirmPassword.text.toString().trim()
        if(!check(email,password,confirmPassword))
            return

        viewModel.registrationDetails.email = email
        viewModel.registrationDetails.password = password

        (requireActivity() as RegistrationActivity).navigator
            .hideCurrent()
            .show(RegistrationGeneral2Fragment.newInstance())
            .navigate()
    }
}