package com.ugochukwu.routine

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ugochukwu.routine.viewmodel.RoutineViewModel
import kotlinx.android.synthetic.main.fragment_routine_detail.*
import java.util.*

class RoutineDetailFragment : BottomSheetDialogFragment() {

    private val routineViewModel: RoutineViewModel by hiltNavGraphViewModels(R.id.navigation_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_routine_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: RoutineDetailFragmentArgs by navArgs()
        val routineId = safeArgs.routineId

        routineViewModel.getRoutine(routineId)

        routineViewModel.routine.observe(viewLifecycleOwner) { routine ->

            label_title.text = routine.title
            label_description.text = routine.description
            val nextTime = getRoutineTimeForComparison(routine)
            val timing = DateUtils.getRelativeTimeSpanString(
                nextTime,
                Date().time, DateUtils.SECOND_IN_MILLIS
            ).toString()
            label_time.text = getString(R.string.next_routine_check, timing.lowercase())

            if (routine.doneCount == 0 && routine.missCount == 0) {
                label_report.isVisible = false
            } else {
                val completionRate =
                    ((routine.doneCount * 100.0) / (routine.doneCount + routine.missCount))
                if (completionRate > 70) {
                    label_report.text = getString(R.string.above_70_percent_report)
                } else {
                    label_report.text = getString(R.string.below_70_percent_report)
                }
                label_report.isVisible = true
            }

            button_edit.setOnClickListener {
                val action = RoutineDetailFragmentDirections.actionDetailToAddEditRoutine()
                action.routine = routine
                findNavController().navigate(action)
                dismiss()
            }

            button_delete.setOnClickListener {
                routineViewModel.deleteRoutine(routine)
                dismiss()
            }

            button_mark_complete.isVisible =
                Calendar.getInstance().timeInMillis > routine.lastRunTime &&
                        Calendar.getInstance().timeInMillis - routine.lastRunTime < FIVE_MINUTES
            button_mark_complete.setOnClickListener {
                routineViewModel.markRoutineAsDone(routine)
            }
        }
    }
}