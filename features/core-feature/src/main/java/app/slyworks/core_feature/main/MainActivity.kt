package app.slyworks.core_feature.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.collection.SimpleArrayMap
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import app.slyworks.base_feature.ActivityUtils
import app.slyworks.base_feature.BaseActivity
import app.slyworks.base_feature.ui.ExitDialog
import app.slyworks.base_feature.ui.LogoutDialog
import app.slyworks.constants_lib.*
import app.slyworks.core_feature.ProfileHostFragment
import app.slyworks.core_feature.R
import app.slyworks.core_feature.chat.ChatHostFragment
import app.slyworks.core_feature.databinding.ActivityMainBinding
import app.slyworks.core_feature.di.ActivityComponent
import app.slyworks.core_feature.home.DoctorHomeFragment
import app.slyworks.core_feature.home.PatientHomeFragment
import app.slyworks.navigation_feature.Navigator
import app.slyworks.navigation_feature.Navigator.Companion.getParcelable
import app.slyworks.navigation_feature.interfaces.FragmentContinuationStateful
import app.slyworks.utils_lib.utils.px
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import javax.inject.Inject

val Context.activityComponent: ActivityComponent
get() = (this as MainActivity)._activityComponent

class MainActivity : BaseActivity(),  NavigationView.OnNavigationItemSelectedListener {
    //region Vars
    lateinit var _activityComponent: ActivityComponent

    private var fragmentMap:SimpleArrayMap<String, Int> = SimpleArrayMap()
    private var fragmentMap2:SimpleArrayMap<String, Int> = SimpleArrayMap()
    private lateinit var fragment:String

    private var selectedItem:Int = -1

    private lateinit var bnvMain:BottomNavigationView

    private lateinit var navigator: FragmentContinuationStateful
    private lateinit var binding:ActivityMainBinding

    @Inject
    lateinit var viewModel: MainActivityViewModel

    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)

        initOffMainThread()
        initUsingAsyncLayoutInflater()
    }

    private fun initOffMainThread(){
        CoroutineScope(Dispatchers.IO).launch {
            initFragmentMap()
            initData()
        }
    }

    private fun initUsingAsyncLayoutInflater():Unit =
        AsyncLayoutInflater(this)
            .inflate(R.layout.activity_main, findViewById<ViewGroup>(android.R.id.content))
            { view, resid, parent ->
                binding = ActivityMainBinding.bind(view)
                setContentView(binding.root)

                initViews()
            }


    private fun initFragmentMap(){
        if(viewModel.getUserAccountType() == DOCTOR){
            fragment = FRAGMENT_DOCTOR_HOME

            fragmentMap.put(FRAGMENT_DOCTOR_HOME, R.id.action_home)
            fragmentMap.put(FRAGMENT_CHAT_HOST, R.id.action_chats)

            fragmentMap2.put(DoctorHomeFragment::class.simpleName, R.id.action_home)
            fragmentMap2.put(ChatHostFragment::class.simpleName, R.id.action_chats)
        } else{
            fragment = FRAGMENT_PATIENT_HOME

            fragmentMap.put(FRAGMENT_PATIENT_HOME, R.id.action_home)
            fragmentMap.put(FRAGMENT_CHAT_HOST, R.id.action_chats)
            fragmentMap.put(FRAGMENT_PROFILE_HOST, R.id.action_connect)

            fragmentMap2.put(PatientHomeFragment::class.simpleName, R.id.action_home)
            fragmentMap2.put(ChatHostFragment::class.simpleName, R.id.action_chats)
            fragmentMap2.put(ProfileHostFragment::class.simpleName, R.id.action_connect)
        }
    }
    private fun initDI(){
        _activityComponent =
        ActivityComponent.getInitialBuilder()
            .appCompatActivity(this)
            .build()
            .also{ it.inject(this@MainActivity) }
    }

    private fun initData(){
        if(intent.hasExtra(EXTRA_MAIN_FRAGMENT))
            fragment = intent.getParcelable(EXTRA_MAIN_FRAGMENT)!!
        bnvMain.setSelectedItemId(fragmentMap[fragment]!!)

        viewModel.subscribeToNetwork().observe(this){
            binding.mainLayout.networkStatusView.isVisible = it
        }

       navigator = Navigator.transactionWithStateFrom(supportFragmentManager)

        this.onBackPressedDispatcher
            .addCallback(this,
            object:OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if(supportFragmentManager.backStackEntryCount == 1){
                        if(ActivityUtils.isLastActivity())
                            ExitDialog.getInstance()
                                .show(supportFragmentManager, "exit dialog")
                        else{
                            isEnabled = false
                            onBackPressed()
                            return
                        }
                    }else{
                        navigator.popBackStack{
                            bnvMain.setSelectedItemId(fragmentMap2[it]!!)
                        }
                    }
                }
            })

    }
    
    private fun initViews(){
        val bnvLayout:View
        if(viewModel.getUserAccountType() == DOCTOR)
            bnvLayout  = LayoutInflater.from(this).inflate(R.layout.menu_doctor, binding.mainLayout.layoutAppBarMain, false)
        else
            bnvLayout = LayoutInflater.from(this).inflate(R.layout.menu_patient,binding.mainLayout.layoutAppBarMain, false)

        bnvLayout.layoutParams =  CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            70.px)
            .apply {
                gravity = Gravity.BOTTOM
            }

        binding.mainLayout.layoutAppBarMain.addView(bnvLayout)

        bnvMain = findViewById(R.id.bnv_main)
        bnvMain.setOnItemSelectedListener(::handleBnvMenuItemClick)
        bnvMain.setOnItemReselectedListener(::handleBnvMenuItemClick)
    }

    private fun handleBnvMenuItemClick(item:MenuItem):Boolean {
        if (item.itemId == selectedItem)
            return true

        return when (item.itemId) {
            R.id.action_home -> {
                _inflateFragment(
                    if (viewModel.getUserAccountType() == DOCTOR)
                        DoctorHomeFragment.getInstance()
                    else
                        PatientHomeFragment.getInstance()
                )
                selectedItem = item.itemId
                true
            }
            R.id.action_chats -> {
                _inflateFragment(ChatHostFragment.getInstance())
                selectedItem = item.itemId
                true
            }
            R.id.action_connect -> {
                _inflateFragment(ProfileHostFragment.getInstance())
                selectedItem = item.itemId
                true
            }
            else -> throw UnsupportedOperationException()

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        toggleDrawerState()

        when(item.getItemId()){
            R.id.action_logout -> {
                LogoutDialog.getInstance()
                    .show(supportFragmentManager, "")
                return true
            }
            R.id.action_call_history ->{
                return true
            }
            R.id.action_requests ->{
                return true
            }
            R.id.action_settings ->{
                return true
            }
            R.id.action_support ->{
                return true
            }
            R.id.action_about ->{
                return true
            }
        }

        return true
    }

    /*called from outside this class*/
    fun inflateFragment(f:String){
        //_inflateFragment(f)
        bnvMain.setSelectedItemId(fragmentMap[f]!!)
    }

    private fun _inflateFragment(f:Fragment){
        navigator
            .into(R.id.fragment_container_main)
            .show(f)
            .navigate()
    }

    fun toggleProgressVisibility() {
        if(binding.progress.isVisible)
            binding.progress.isVisible = false
        else if(!binding.progress.isVisible)
            binding.progress.isVisible = true
    }

    fun toggleDrawerState():Unit = if(binding.drawerMain.isOpen) binding.drawerMain.close() else binding.drawerMain.open()
}