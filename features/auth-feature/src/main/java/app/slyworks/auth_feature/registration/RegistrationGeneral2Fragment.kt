 package app.slyworks.auth_feature.registration

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import app.slyworks.auth_feature.IRegViewModel
import app.slyworks.auth_feature.databinding.FragmentRegistrationGeneral2Binding
import app.slyworks.base_feature.PermissionManager
import app.slyworks.base_feature.PermissionStatus
import app.slyworks.base_feature.ui.ChangePhotoDialog
import app.slyworks.models_commons_lib.models.AccountType
import app.slyworks.models_commons_lib.models.Gender
import app.slyworks.models_commons_lib.models.Outcome
import app.slyworks.utils_lib.ContentResolverStore
import app.slyworks.utils_lib.utils.closeKeyboard3
import app.slyworks.utils_lib.utils.plusAssign
import app.slyworks.utils_lib.utils.displayImage
import app.slyworks.utils_lib.utils.displayMessage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

 class RegistrationGeneral2Fragment : Fragment() {
    //region Vars
    private lateinit var binding: FragmentRegistrationGeneral2Binding
    private lateinit var viewModel: RegistrationActivityViewModel
    private var permissionManager:PermissionManager = PermissionManager()
    private val disposables = CompositeDisposable()

     private var date:String = ""
     private var gender: Gender = Gender.NOT_SET
     private lateinit var imageUri:Uri
     private var hasProfileImageBeenSelected:Boolean = false
   //endregion

    companion object {
        @JvmStatic
        fun newInstance(): RegistrationGeneral2Fragment = RegistrationGeneral2Fragment()
    }

     override fun onAttach(context: Context) {
         super.onAttach(context)
         viewModel = (context as RegistrationActivity).viewModel
     }

     override fun onDestroy() {
         super.onDestroy()

         ContentResolverStore.nullifyContentResolver()

         disposables.clear()
     }

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

         initPermissions()
     }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegistrationGeneral2Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         initData()
         initViews()
     }

     private fun initPermissions(){
         /*has to be done in onCreate()*/
         permissionManager.initialize(
                 this,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE,
                 Manifest.permission.CAMERA)
     }

     private fun initData(){
         ContentResolverStore.setContentResolver(requireActivity().contentResolver)

         viewModel.progressLiveData.observe(viewLifecycleOwner){
             (requireActivity() as IRegViewModel).toggleProgressView(it)
         }

         viewModel.profileImageUriLiveData.observe(viewLifecycleOwner){
             binding.ivProfile.displayImage(it)
             imageUri = it
             hasProfileImageBeenSelected = true
         }

         disposables +=
         permissionManager
             .getPermissionsObservable()
             .subscribeOn(AndroidSchedulers.mainThread())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe{ o: Outcome ->
                 when{
                     o.isSuccess -> {
                         ChangePhotoDialog.getInstance().apply {
                             viewModel.handleProfileImageUri(this.getObservable())
                         }
                             .show(requireActivity().supportFragmentManager, "")
                     }
                     o.isFailure -> {
                         val l:List<PermissionStatus> = o.getTypedValue<List<PermissionStatus>>()
                         displayMessage("Medix requires these permissions to work", binding.root)
                     }
                 }
             }
     }

     private fun initViews(){
         val genders:MutableList<String> = mutableListOf("Male", "Female")
         val genderSpinnerAdapter:ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item, genders)
         genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         binding.spinnerGender.setAdapter(genderSpinnerAdapter)
         binding.spinnerGender.setOnItemSelectedListener(
             object : AdapterView.OnItemSelectedListener{
                 override fun onNothingSelected(p0: AdapterView<*>?) {}
                 override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                   if(position == 0)
                       gender = Gender.MALE
                   else if(position == 1)
                       gender = Gender.FEMALE
                 }

             })

         binding.ivProfile.setOnClickListener {
             permissionManager.requestPermissions()
         }

         binding.etLastName.setOnEditorActionListener(
             TextView.OnEditorActionListener{ p0, p1, p2 ->
                 if(p0!!.id == binding.etLastName.id){
                     requireActivity().closeKeyboard3()
                     binding.datePicker.requestFocus()
                     return@OnEditorActionListener true
                 }

                 return@OnEditorActionListener false
             })

         binding.datePicker.setOnClickListener{
             val calendar: Calendar = SimpleDateFormat.getDateInstance().calendar
             val year:Int = calendar.get(Calendar.YEAR)
             val month:Int = calendar.get(Calendar.MONTH)
             val day:Int = calendar.get(Calendar.DAY_OF_MONTH)

             val datePickerDialog = DatePickerDialog(requireContext(),
                 DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                     date = "${day}/${month + 1}/${year}"
                     binding.datePicker.setText(date)
                 }, year, month, day)

             datePickerDialog.updateDate(1990, 1, 1)
             datePickerDialog.show()
         }

         binding.btnNext.setOnClickListener {
             requireActivity().closeKeyboard3()

             if(!check())
                 return@setOnClickListener

             val firstName:String = binding.etFirstName.text.toString().trim()
             val lastName:String = binding.etLastName.text.toString().trim()

             viewModel.registrationDetails.firstName = firstName
             viewModel.registrationDetails.lastName = lastName
             viewModel.registrationDetails.age = date
             viewModel.registrationDetails.sex = gender
             val f =
                 if(viewModel.registrationDetails.accountType == AccountType.PATIENT)
                     RegistrationPatientFragment.newInstance()
                 else
                     RegistrationDoctorFragment.newInstance()

             (requireActivity() as RegistrationActivity).navigator
                 .hideCurrent()
                 .show(f)
                 .navigate()
         }
     }

     private fun check():Boolean {
         val firstName:String = binding.etFirstName.text.toString().trim()
         val lastName:String = binding.etLastName.text.toString().trim()

         var status = true

         if(TextUtils.isEmpty(firstName)){
             displayMessage("please enter a first name", binding.root)
             status = false
         } else if(TextUtils.isEmpty(lastName)){
             displayMessage("please enter a last name", binding.root)
             status = false
         } else if(!hasProfileImageBeenSelected){
             displayMessage("a profile image is required", binding.root)
             status = false
         } else if(date == ""){
             displayMessage("please enter your date of birth", binding.root)
             status = false
         } else if(gender == Gender.NOT_SET){
             displayMessage("please select your gender", binding.root)
             status = false
         }

         return status
     }

}