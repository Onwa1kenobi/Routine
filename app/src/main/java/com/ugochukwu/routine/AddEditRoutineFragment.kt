package com.ugochukwu.routine


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.ugochukwu.routine.data.model.Frequency
import com.ugochukwu.routine.data.model.Routine
import com.ugochukwu.routine.viewmodel.RoutineViewContract
import com.ugochukwu.routine.viewmodel.RoutineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_routine.*
import java.util.*

@AndroidEntryPoint
class AddEditRoutineFragment : Fragment() {

    private val routineViewModel: RoutineViewModel by hiltNavGraphViewModels(R.id.navigation_graph)

    private var frequency = Frequency.HOURLY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_routine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: AddEditRoutineFragmentArgs by navArgs()

        val routine: Routine? = safeArgs.routine

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        category_toggle_group.forEach { button ->
            button.setOnClickListener { (button as MaterialButton).isChecked = true }
        }
        category_toggle_group.addOnButtonCheckedListener { _, checkedId, _ ->
            frequency = when (checkedId) {
                R.id.button_hourly -> Frequency.HOURLY
                R.id.button_daily -> Frequency.DAILY
                R.id.button_weekly -> Frequency.WEEKLY
                R.id.button_monthly -> Frequency.MONTHLY
                R.id.button_yearly -> Frequency.YEARLY
                else -> Frequency.HOURLY
            }
            label_frequency.text = String.format(
                "${getString(R.string.frequency)}: %s",
                frequency.name.lowercase().replaceFirstChar { it.uppercase() })
        }


        if (routine != null) {
            field_title.setText(routine.title)
            field_description.setText(routine.description)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = routine.lastRunTime

            if (Build.VERSION.SDK_INT >= 23) {
                time_picker.hour = calendar.get(Calendar.HOUR)
                time_picker.minute = calendar.get(Calendar.MINUTE)
            } else {
                time_picker.currentHour = calendar.get(Calendar.HOUR)
                time_picker.currentMinute = calendar.get(Calendar.MINUTE)
            }

            frequency = routine.frequency
            when (routine.frequency) {
                Frequency.HOURLY -> button_hourly.performClick()
                Frequency.DAILY -> button_daily.performClick()
                Frequency.WEEKLY -> button_weekly.performClick()
                Frequency.MONTHLY -> button_monthly.performClick()
                Frequency.YEARLY -> button_yearly.performClick()
            }
        } else {
            button_hourly.performClick()
        }

        button_done.setOnClickListener {
            layout_title_field_wrapper.isErrorEnabled = false
            layout_description_field_wrapper.isErrorEnabled = false

            if (field_title.text.toString().trim().isEmpty()) {
                layout_title_field_wrapper.error = "Please enter a title for your routine"
            } else if (field_description.text.toString().trim().isEmpty()) {
                layout_description_field_wrapper.error =
                    "Please enter a description for your routine"
            } else {
                val hour: Int
                val minute: Int

                if (Build.VERSION.SDK_INT >= 23) {
                    hour = time_picker.hour
                    minute = time_picker.minute
                } else {
                    hour = time_picker.currentHour
                    minute = time_picker.currentMinute
                }

                if (routine == null) {
                    // New routine, call viewModel to add
                    routineViewModel.saveRoutine(
                        field_title.text.toString().trim(),
                        field_description.text.toString().trim(),
                        hour, minute,
                        frequency
                    )
                } else {
                    // Existing routine, call viewModel to update
                    routineViewModel.updateRoutine(
                        routine,
                        field_title.text.toString().trim(),
                        field_description.text.toString().trim(),
                        hour, minute,
                        frequency
                    )
                }
            }
        }

        routineViewModel.routineViewContract.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { info ->
                when (info) {
                    is RoutineViewContract.MessageDisplay -> {
                        // Display message
                        Toast.makeText(context, info.message, Toast.LENGTH_SHORT).show()
                    }

                    is RoutineViewContract.SaveSuccess -> {
                        // Close fragment
                        findNavController().popBackStack()
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        })
    }
}
