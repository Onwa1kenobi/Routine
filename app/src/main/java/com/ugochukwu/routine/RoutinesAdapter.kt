package com.ugochukwu.routine

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ugochukwu.routine.data.model.Routine
import kotlinx.android.synthetic.main.item_routine.view.label_description
import kotlinx.android.synthetic.main.item_routine.view.label_title
import kotlinx.android.synthetic.main.item_routine_next_up.view.*
import java.util.*

class RoutinesAdapter(
    private val isNextUp: Boolean = false,
    private val onRoutineClicked: (Routine) -> Unit = { }
) :
    PagingDataAdapter<RoutinesAdapter.UiRoutineModel, RecyclerView.ViewHolder>(RoutineDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isNextUp) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_routine_next_up, parent, false)
            NextUpViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_routine, parent, false)

            RoutineViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val routine = getItem(position) as? UiRoutineModel.RoutineItem
        routine?.let {
            if (isNextUp) {
                (holder as NextUpViewHolder).bind(it.routine)
            } else {
                (holder as RoutineViewHolder).bind(it.routine)
            }
        }
    }

    inner class RoutineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(routine: Routine) {
            itemView.setOnClickListener { onRoutineClicked(routine) }

            itemView.label_title.text = routine.title
            itemView.label_description.text = routine.description
        }
    }

    inner class NextUpViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(routine: Routine) {
            itemView.setOnClickListener { onRoutineClicked(routine) }

            itemView.label_title.text = routine.title
            itemView.label_description.text = routine.description

            val now = Date().time
            Log.e(
                "RT", """
                ${routine.lastRunTime} < ${now} is ${routine.lastRunTime < now}
            """.trimIndent()
            )

            val timing = DateUtils.getRelativeTimeSpanString(
                getRoutineTimeForComparison(routine),
                now, DateUtils.SECOND_IN_MILLIS
            ).toString()
            itemView.label_time.text = timing
        }
    }

    private class RoutineDiffUtil : DiffUtil.ItemCallback<UiRoutineModel>() {
        override fun areItemsTheSame(
            oldItem: UiRoutineModel,
            newItem: UiRoutineModel
        ): Boolean {
            return if (oldItem is UiRoutineModel.RoutineItem && newItem is UiRoutineModel.RoutineItem) {
                oldItem.routine.id == newItem.routine.id
            } else false
        }

        override fun areContentsTheSame(
            oldItem: UiRoutineModel,
            newItem: UiRoutineModel
        ): Boolean {
            return if (oldItem is UiRoutineModel.RoutineItem && newItem is UiRoutineModel.RoutineItem) {
                oldItem.routine.id == newItem.routine.id
                        && oldItem.routine.title == newItem.routine.title
                        && oldItem.routine.description == newItem.routine.description
                        && oldItem.routine.frequency == newItem.routine.frequency
                        && oldItem.routine.lastRunTime == newItem.routine.lastRunTime
                        && oldItem.routine == newItem.routine
            } else false
        }
    }

    sealed class UiRoutineModel {
        data class RoutineItem(val routine: Routine) : UiRoutineModel()
    }
}