package com.example.gdgandroidwebinar7

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class CalendarJobService : JobService() {

    private val calendarScope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        calendarScope.launch {
            delay(1500)

            //JobService może działać zarówno jako foreground service jak i jako background service
            for (index in 1..10) {
                Log.w(TAG, "working $index")
                delay(1500)
            }

            if (TRIGGER_PERIODIC_JOB)
                scheduleCalendarJob(this@CalendarJobService.applicationContext)

            jobFinished(params, false)
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    companion object {
        private val TAG = CalendarJobService::class.simpleName
        private const val MY_BACKGROUND_JOB = 0
        var TRIGGER_PERIODIC_JOB = true

        fun scheduleCalendarJob(context: Context) {
            Log.w(TAG, "scheduleCalendarJob")

            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val job = JobInfo.Builder(
                MY_BACKGROUND_JOB,
                ComponentName(context, CalendarJobService::class.java)
            )
                .addTriggerContentUri(
                    JobInfo.TriggerContentUri(
                        CalendarContract.Calendars.CONTENT_URI,
                        JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS
                    )
                )
                .build()
            jobScheduler.schedule(job)
        }
    }
}