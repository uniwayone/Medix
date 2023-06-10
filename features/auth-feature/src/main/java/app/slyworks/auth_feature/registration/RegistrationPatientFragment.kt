package app.slyworks.auth_feature.registration

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.KeyListener
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.R
import app.slyworks.auth_feature.databinding.FragmentRegistrationPatientBinding
import app.slyworks.auth_lib.VerificationDetails
import app.slyworks.base_feature.ui.TermsAndConditionsBSDialog
import app.slyworks.constants_lib.MAIN_ACTIVITY_INTENT_FILTER
import app.slyworks.navigation_feature.Navigator
import app.slyworks.utils_lib.utils.plusAssign
import app.slyworks.utils_lib.utils.displayMessage
import app.slyworks.utils_lib.utils.px
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RegistrationPatientFragment : Fragment() {
    //region Vars
    private lateinit var binding: FragmentRegistrationPatientBinding
    private lateinit var viewModel: RegistrationActivityViewModel

    private var createdLayoutsList:MutableList<View> = mutableListOf()
    private val FALLBACK_ID:Int = R.id._view_horizontal_1
    private var lastCreatedViewID:Int = FALLBACK_ID
    private var isThereCreatedLayout:Boolean = false

    private val disposables = CompositeDisposable()
    //endregion

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = (context as RegistrationActivity).viewModel
    }

    companion object {
        @JvmStatic
        fun newInstance(): RegistrationPatientFragment = RegistrationPatientFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegistrationPatientBinding.inflate(layoutInflater, container, false)
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
       viewModel.messageLiveData.observe(viewLifecycleOwner){ displayMessage(it, binding.root) }
       viewModel.verificationSuccessfulLiveData.observe(viewLifecycleOwner){ _ ->
           Navigator.intentFor(requireContext(), MAIN_ACTIVITY_INTENT_FILTER)
               .newAndClearTask()
               .navigate()
       }

       viewModel.registrationSuccessfulLiveData.observe(viewLifecycleOwner){
           val dialog = SelectVerificationMethodBSDialog.getInstance()
           disposables +=
           dialog.getSubject()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe {
                     if(it == VerificationDetails.OTP) {
                         (requireActivity() as RegistrationActivity).navigator
                             .hideCurrent()
                             .show(RegistrationOTP1Fragment.newInstance())
                             .navigate()
                       return@subscribe
                     }

                    viewModel.verifyByEmail()
                 }

           dialog.show(requireActivity().supportFragmentManager,"")
       }
    }

    private fun initViews(){
        val spannableText:SpannableString = SpannableString("I have read and agree to the Terms and Conditions")
        val clickableSpan:ClickableSpan =
           object : ClickableSpan() {
               override fun updateDrawState(ds: TextPaint) {
                   super.updateDrawState(ds)
                   ds.setUnderlineText(true)
               }

               override fun onClick(p0: View) {
                  TermsAndConditionsBSDialog.getInstance()
                      .show(childFragmentManager, "")
               }
           }
        
        spannableText.setSpan(
            clickableSpan,
            spannableText.length - 21,
            spannableText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE )

        /* remember to set textColorLink*/
        binding.tvTandC.setText(spannableText)
        binding.tvTandC.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvTandC.setHighlightColor(Color.TRANSPARENT)

        binding.cbTandC.setOnCheckedChangeListener { _, status ->
            binding.btnSignUp.setEnabled(status)
        }

        binding.btnAddDiseases.setOnClickListener{
            if (createdLayoutsList.isNotEmpty() ){
                val textView:EditText = createdLayoutsList.last()
                    .findViewById(app.slyworks.base_feature.R.id.etText_layout_text)

                if(TextUtils.isEmpty(textView.text)){
                    displayMessage("please fill existing text fields before adding another", binding.root)
                    return@setOnClickListener
                }

                addLayout()
                return@setOnClickListener
            }

            //assuming no layout has been added before now
            addLayout()
        }

        binding.btnSignUp.setOnClickListener {
            if(!check())
                return@setOnClickListener

            viewModel.registrationDetails.history = parseUserHistory()

            viewModel.registerUser()
        }
    }

    private fun check():Boolean {
        var status = true
        if(binding.cbTandC.isChecked().not()){
            displayMessage("you need to accept the Terms and Conditions to proceed", binding.root)
            status = false
        }else if(isThereCreatedLayout){
            createdLayoutsList.forEach { view ->
                val editText:EditText = view.findViewById(app.slyworks.base_feature.R.id.etText_layout_text)
                if(TextUtils.isEmpty(editText.text)){
                    displayMessage("please fill in all added history texts or remove them", binding.root)
                    status = false
                }
            }
        }

        return status
    }

    private fun parseUserHistory():MutableList<String>{
        val list = mutableListOf<CheckBox>(
            binding.cbHypertension,
            binding.cbDiabetes,
            binding.cbAsthma)

        val list2:List<String> =
            list.filter { it.isChecked }
                .map { it.text.toString() }

        val list3 = mutableListOf<String>()
        if(createdLayoutsList.isNotEmpty()){
            createdLayoutsList.forEach { layout ->
                list3.add(layout.findViewById<EditText>(app.slyworks.base_feature.R.id.etText_layout_text).text.toString().trim())
            }
        }

        return mutableListOf<String>().plus(list2).plus(list3) as MutableList<String>
    }


    private fun addLayout() {
        val inflater: LayoutInflater = LayoutInflater.from(requireContext())

        val layout: View = inflater.inflate(app.slyworks.base_feature.R.layout.layout_text, binding.container, false)
        layout.setId(View.generateViewId())

        val constraintSet: ConstraintSet = ConstraintSet()
        constraintSet.clone(binding.container)

        val layoutParams: ViewGroup.LayoutParams = layout.layoutParams
        layoutParams.height = 55

        val ivCancel: ImageView = layout.findViewById(app.slyworks.base_feature.R.id.ivCancel)
        ivCancel.setOnClickListener { removeCreatedView(layout) }

        val lp:ViewGroup.LayoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.px)
        val divider:View = View(context)
        divider.setLayoutParams(lp)
        divider.setId(View.generateViewId())

        binding.container.addView(layout)
        binding.container.addView(divider)

        constraintSet.connect(layout.id, ConstraintSet.START, binding.container.id, ConstraintSet.START)
        constraintSet.connect(layout.id, ConstraintSet.END, binding.container.id, ConstraintSet.END)
        constraintSet.connect(
            layout.id,
            ConstraintSet.TOP,
            getAppropriateId(),
            ConstraintSet.BOTTOM,
            resources.getDimensionPixelSize(app.slyworks.base_feature.R.dimen.layout_size_margin)
        )

        constraintSet.constrainWidth(layout.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(layout.id, ConstraintSet.WRAP_CONTENT)

        setAppropriateId(layout.id)

        constraintSet.connect(divider.id, ConstraintSet.START, binding.container.id, ConstraintSet.START)
        constraintSet.connect(divider.id, ConstraintSet.END, binding.container.id, ConstraintSet.END)
        constraintSet.connect(
            divider.id,
            ConstraintSet.TOP,
            getAppropriateId(),
            ConstraintSet.BOTTOM,
            resources.getDimensionPixelSize(app.slyworks.base_feature.R.dimen.divider_top_margin)
        )

        constraintSet.constrainWidth(layout.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(layout.id, ConstraintSet.WRAP_CONTENT)

        setAppropriateId(divider.id)

        constraintSet.connect(binding.btnAddDiseases.id, ConstraintSet.START, binding.container.id, ConstraintSet.START)
        constraintSet.connect(binding.btnAddDiseases.id, ConstraintSet.END, binding.container.id, ConstraintSet.END)
        constraintSet.connect(
            binding.btnAddDiseases.id,
            ConstraintSet.TOP,
            layout.id,
            ConstraintSet.BOTTOM,
            resources.getDimensionPixelSize(app.slyworks.base_feature.R.dimen.layout_size_margin2)
        )

        constraintSet.applyTo(binding.container)
        layout.alpha = 0F
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
            layout,
            "alpha",
            0F,
            1F)
        objectAnimator.duration = 1_500
        objectAnimator.start()

        isThereCreatedLayout = true

        saveView(layout)

        redrawViews()
    }

    private fun removeCreatedView(layout:View){
        binding.container.removeView(layout)
        createdLayoutsList.remove(layout)

        if(createdLayoutsList.isEmpty())
            isThereCreatedLayout = false

        setAppropriateId(createdLayoutsList.lastOrNull()?.id ?: FALLBACK_ID)
        realignButton()
        redrawViews()
    }

    private fun saveView(layout:View):Boolean = createdLayoutsList.add(layout)

    private fun realignButton(){
        val constraintSet: ConstraintSet = ConstraintSet()
        constraintSet.clone(binding.container)

        constraintSet.connect(binding.btnAddDiseases.id, ConstraintSet.START, binding.container.id, ConstraintSet.START)
        constraintSet.connect(binding.btnAddDiseases.id, ConstraintSet.END, binding.container.id, ConstraintSet.END)
        constraintSet.connect(
            binding.btnAddDiseases.id,
            ConstraintSet.TOP,
            getAppropriateId(),
            ConstraintSet.BOTTOM,
            resources.getDimensionPixelSize(app.slyworks.base_feature.R.dimen.layout_size_margin2))

        constraintSet.applyTo(binding.container)
    }

    private fun redrawViews(){
        if(createdLayoutsList.isEmpty()) return

        createdLayoutsList.forEach { constraintLayout ->
            val editText:EditText = constraintLayout.findViewById(app.slyworks.base_feature.R.id.etText_layout_text)
            val imageView:ImageView = constraintLayout.findViewById(app.slyworks.base_feature.R.id.ivCancel)

            toggleEditState(editText, false)
            imageView.visibility = View.INVISIBLE
            if(constraintLayout == createdLayoutsList.last()){
                toggleEditState(editText, true)
                imageView.visibility = View.VISIBLE
            }
        }

    }

    private fun toggleEditState(editText: EditText, status:Boolean){
        if (status) {
            editText.keyListener = editText.tag as KeyListener
        }else{
            if(editText.keyListener == null) return

            editText.tag = editText.keyListener
            editText.keyListener = null
        }
    }

    private fun setAppropriateId(id:Int){ lastCreatedViewID = id }
    private fun getAppropriateId():Int = lastCreatedViewID
}