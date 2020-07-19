package com.example.gdgandroidwebinar7

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.annotation.RequiresPermission
import java.util.*

class CalendarManager {

    private val PROJECTION_ID_INDEX: Int = 0
    private val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
    private val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
    private val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3
    private val PROJECTION_TYPE_INDEX: Int = 4

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR ])
    fun getCalendars(context: Context) {
        TODO("Not yet implemented")
    }

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR ])
    fun createCalendar(context: Context, calendarName: String): Long? {
        TODO("Not yet implemented")
    }

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR ])
    fun getCalendarId(context: Context, calendarName: String): Long? {
        TODO("Not yet implemented")
    }

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR ])
    fun addEvent(
        context: Context,
        date: Date,
        calendarId: Long,
        eventTitle: String,
        eventDescription: String
    ): Uri? {
        TODO("Not yet implemented")
    }
}