package com.ugochukwu.routine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ugochukwu.routine.data.model.Routine
import com.ugochukwu.routine.viewmodel.RoutineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val routineViewModel: RoutineViewModel by hiltNavGraphViewModels(R.id.navigation_graph)

    private val routinesAdapter by lazy {
        RoutinesAdapter(
            onRoutineClicked = {
                navigateToRoutineDetail(it)
            }
        )
    }

    private val nextUpRoutinesAdapter by lazy {
        RoutinesAdapter(
            isNextUp = true,
            onRoutineClicked = {
                navigateToRoutineDetail(it)
            }
        )
    }

    private fun navigateToRoutineDetail(routine: Routine) {
        val action = HomeFragmentDirections.actionHomeToRoutineDetail(routine.id)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Click listener for the floating action button
        fab.setOnClickListener {
            // Navigate to add/edit view
            val action = HomeFragmentDirections.actionHomeToAddEditRoutine()
            findNavController().navigate(action)
        }

        (recycler_view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recycler_view.itemAnimator = null
        recycler_view.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        recycler_view.adapter = routinesAdapter

        (recycler_view_next_up.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recycler_view_next_up.itemAnimator = null
        recycler_view_next_up.adapter = nextUpRoutinesAdapter

        routinesAdapter.addLoadStateListener { loadState ->
            if (isAdded) {
                // show empty list
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && routinesAdapter.itemCount == 0
                label_no_routine.isVisible = isListEmpty

                // Only show the list if refresh succeeds.
                recycler_view.isVisible = loadState.refresh is LoadState.NotLoading
            }
        }

        nextUpRoutinesAdapter.addLoadStateListener { loadState ->
            if (isAdded) {
                // show empty list
                val isListEmpty = nextUpRoutinesAdapter.itemCount == 0
                tv_next_up.isVisible = isListEmpty.not()
                recycler_view_next_up.isVisible = isListEmpty.not()
            }
        }

        lifecycleScope.launch {
            routineViewModel.getRoutines().collectLatest { data ->
                routinesAdapter.submitData(data)
            }
        }

        lifecycleScope.launch {
            routineViewModel.getNextUpRoutines().collectLatest { data ->
                nextUpRoutinesAdapter.submitData(data)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_TIME_TICK)
        context?.registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(receiver)
    }

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                nextUpRoutinesAdapter.refresh()
            }
        }
    }
}
