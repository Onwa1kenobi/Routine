package com.ugochukwu.routine.alarm

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.ugochukwu.routine.FIVE_MINUTES
import com.ugochukwu.routine.TEN_MINUTES
import com.ugochukwu.routine.data.model.Routine
import com.ugochukwu.routine.getRoutineTimeForComparison
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class AlarmManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    AlarmManager {
    override fun registerAlarm(routine: Routine) {
        val pendingIntent = getRegistrationPendingIntent(routine.id)
        val am = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val alarmTime = getRoutineTimeForComparison(routine)
        am.setRepeating(
            android.app.AlarmManager.RTC_WAKEUP,
            alarmTime - FIVE_MINUTES,
            routine.frequency.value,
            pendingIntent
        )

        val now = Calendar.getInstance().timeInMillis
        if (alarmTime > now && (alarmTime - now) < FIVE_MINUTES) {
            // Instant routine. register miss alarm
            registerMissRoutineAlarm(routine.id)
        }
    }

    override fun cancelAlarm(routine: Routine) {
        val pendingIntent = getCancellationPendingIntent(routine.id)
        val am = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        am.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun registerMissRoutineAlarm(routineId: Int) {
        val pendingIntent = getRegistrationPendingIntent(routineId.unaryMinus())
        val am = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        am.setExact(
            android.app.AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + TEN_MINUTES,
            pendingIntent
        )
    }

    override fun cancelMissRoutineAlarm(routineId: Int) {
        val pendingIntent = getCancellationPendingIntent(routineId.unaryMinus())
        val am = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        am.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun getRegistrationPendingIntent(id: Int): PendingIntent {
        val receiver = ComponentName(context, AlarmReceiver::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("id", id)

        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getCancellationPendingIntent(id: Int): PendingIntent {
        val receiver = ComponentName(context, AlarmReceiver::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent = Intent(context, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}