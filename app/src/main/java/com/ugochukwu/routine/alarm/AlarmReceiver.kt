package com.ugochukwu.routine.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.ugochukwu.routine.MainActivity
import com.ugochukwu.routine.R
import com.ugochukwu.routine.data.RoutineRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver(), CoroutineScope {

    @Inject
    lateinit var repository: RoutineRepository

    @Inject
    lateinit var alarmManager: AlarmManager

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action != null) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                launch {
                    val routines = withContext(Dispatchers.IO) {
                        repository.getAllRoutines()
                    }

                    routines.forEach {
                        // Re-register the alarms
                        alarmManager.registerAlarm(it)
                    }
                }
                return
            }
        }

        // Alarm received, fetch the routine and trigger the notification
        launch {
            val id = intent.getIntExtra("id", 1)
            if (id < 0) {
                // A missed routine
                withContext(Dispatchers.IO) {
                    repository.incrementMissCount(id.unaryMinus())
                }
            } else {
                val routine = withContext(Dispatchers.IO) {
                    repository.getRoutine(id)
                }

                alarmManager.registerMissRoutineAlarm(routine.id)

                showNotification(
                    context,
                    MainActivity::class.java,
                    routine.title,
                    routine.description,
                    routine.id
                )
            }
        }
    }

    private fun showNotification(
        context: Context,
        cls: Class<*>,
        title: String,
        content: String,
        requestCode: Int
    ) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelId = "routine_channel_$requestCode"

        val notificationIntent = Intent(context, cls)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(cls)
        stackBuilder.addNextIntent(notificationIntent)

        val pendingIntent =
            stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, channelId)

        val notification = builder.setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(requestCode, notification)
    }
}
