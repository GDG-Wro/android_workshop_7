package com.example.gdgandroidwebinar7

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var hasPeriodicUpdates = false
    private val CALENDAR_REQUEST_CODE = 111

    private val calendarManager by lazy {
        CalendarManager()
    }

    private val calendarReceiver by lazy {
        CalendarUpdateReceiver()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
            CALENDAR_REQUEST_CODE
        )

        registerCalendarReceiver()
        //CalendarJobService.scheduleCalendarJob(this)

        periodicUpdateSwitch.setOnCheckedChangeListener { _, isChecked ->
            hasPeriodicUpdates = isChecked
            CalendarJobService.TRIGGER_PERIODIC_JOB = hasPeriodicUpdates
        }

        addCalendarButton.setOnClickListener {
            if (isPermissionGranted(Manifest.permission.READ_CALENDAR)
                && isPermissionGranted(Manifest.permission.WRITE_CALENDAR)
            ) {
                val id = calendarManager.createCalendar(this, newCalendarNameText.text.toString())

                if (id != null) {
                    Toast.makeText(this@MainActivity, "Calendar created", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addEventButton.setOnClickListener {
            if (isPermissionGranted(Manifest.permission.READ_CALENDAR)
                && isPermissionGranted(Manifest.permission.WRITE_CALENDAR)
            ) {
                val calendarId = calendarManager.getCalendarId(
                    this@MainActivity,
                    calendarNameText.text.toString()
                )

                calendarId?.let {
                    calendarManager.addEvent(
                        this@MainActivity,
                        Date(),
                        calendarId,
                        eventNameText.text.toString(),
                        eventDescriptionText.text.toString()
                    )
                    Toast.makeText(this@MainActivity, "Event added", Toast.LENGTH_LONG).show()
                } ?: run {
                    Toast.makeText(
                        this@MainActivity,
                        "No calendar with this name",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        if (isPermissionGranted(Manifest.permission.READ_CALENDAR)
            && isPermissionGranted(Manifest.permission.WRITE_CALENDAR)
        ) {
            calendarManager.getCalendars(this)
        }
    }

    private fun registerCalendarReceiver() {
        val filter = IntentFilter(Intent.ACTION_PROVIDER_CHANGED).apply {
            addDataScheme("content")
            addDataAuthority(CalendarContract.Calendars.CONTENT_URI.host, null)
        }

        registerReceiver(calendarReceiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(calendarReceiver)
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CALENDAR_REQUEST_CODE
            && grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED
        ) {
            calendarManager.getCalendars(this)
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun Context.isPermissionGranted(permission: String): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
    }
}
