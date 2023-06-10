package app.slyworks.core_feature.calls_history

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.base_feature.custom_views.CustomDividerDecorator
import app.slyworks.core_feature.R
import app.slyworks.core_feature.RVCallsHistoryAdapter
import app.slyworks.core_feature.chat.ChatHostFragment
import app.slyworks.core_feature.chat.ChatHostFragmentViewModel
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CallsHistoryFragment : Fragment() {
    //region Vars
    private lateinit var rootView:CoordinatorLayout
    private lateinit var progress:ProgressBar
    private lateinit var rvCallsHistory:RecyclerView
    private lateinit var errorLayout:ConstraintLayout
    private lateinit var tvError:TextView
    private lateinit var fabStartCalls:FloatingActionButton

    private lateinit var adapter: RVCallsHistoryAdapter
    private lateinit var viewModel: ChatHostFragmentViewModel
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): CallsHistoryFragment = CallsHistoryFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = (parentFragment as ChatHostFragment).viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calls_history, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initData()
    }

    private fun initViews(view:View){
        rootView = view.findViewById(R.id.rootView)
        progress = view.findViewById(R.id.progress_layout)
        rvCallsHistory = view.findViewById(R.id.rvCalls_calls_history)
        errorLayout = view.findViewById(R.id.errorLayout_frag_calls_history)
        tvError = view.findViewById(R.id.tvError_frag_calls_history)
        fabStartCalls = view.findViewById(R.id.fab_start_new_call)

        adapter = RVCallsHistoryAdapter(viewModel.timeHelper)
        rvCallsHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvCallsHistory.addItemDecoration(
            CustomDividerDecorator<MaterialDivider>(id = R.id.divider_horizontal_1))
        rvCallsHistory.adapter = adapter
    }

    private fun initData(){
        viewModel.progressState.observe(viewLifecycleOwner){
            progress.isVisible = it
        }

        viewModel.errorState.observe(viewLifecycleOwner){
            errorLayout.isVisible = it
            if(it)
                tvError.text = viewModel.errorData.value
        }

        viewModel.observeCallsHistory().observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }
}