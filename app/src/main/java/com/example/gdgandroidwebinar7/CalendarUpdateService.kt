package com.example.gdgandroidwebinar7

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CalendarUpdateService : Service() {
    private val TAG = CalendarUpdateReceiver::class.simpleName

    private val calendarScope = CoroutineScope(Dispatchers.IO)

    val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val NOTIFICATION_CHANNEL_ID = "GDG_TEST_CHANNEL_ID"
    private val NOTIFICATION_CHANNEL_NAME = "GDG webinar notifications"
    private val CALENDAR_NOTIFICATION_ID = 1

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createGeneralNotificationsChannel()

        val notificationCompat = createNotification(0)

        startForeground(CALENDAR_NOTIFICATION_ID, notificationCompat)

        calendarScope.launch {
            delay(1500)

            for (index in 1..10) {
                Log.w(TAG, "working $index")
                notificationManager.notify(CALENDAR_NOTIFICATION_ID, createNotification(index * 10))
                delay(1500)
            }

            stopSelf()
        }
    }

    override fun onDestroy() {
        Log.w(TAG, "finished")
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGeneralNotificationsChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            importance
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(progress: Int): Notification {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("GDG webinar")
            .setContentText("Syncing calendar: $progress %")
            .setOngoing(true)

        return builder.build()
    }
}