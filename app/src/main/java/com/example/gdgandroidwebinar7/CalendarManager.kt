package com.example.gdgandroidwebinar7

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CalendarManager {

    private val calendarScope = CoroutineScope(Dispatchers.IO)
    private val TAG = CalendarManager::class.simpleName

    private val PROJECTION_ID_INDEX: Int = 0
    private val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
    private val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
    private val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3
    private val PROJECTION_TYPE_INDEX: Int = 4

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR])
    fun getCalendars(context: Context) {
        val projection: Array<String> = arrayOf(
            CalendarContract.Calendars._ID,                     // 0
            CalendarContract.Calendars.ACCOUNT_NAME,            // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,           // 3
            CalendarContract.Calendars.ACCOUNT_TYPE             // 4
        )

        calendarScope.launch {
            val uri: Uri = CalendarContract.Calendars.CONTENT_URI
            val cur: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            cur?.use { cursor ->
                while (cursor.moveToNext()) {
                    val calID: Long = cursor.getLong(PROJECTION_ID_INDEX)
                    val displayName: String = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)
                    val accountName: String? = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                    val ownerName: String? = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
                    val type: String? = cursor.getString(PROJECTION_TYPE_INDEX)

                    Log.w(
                        TAG,
                        "calendar:: calID: $calID   displayName: $displayName   accountName: $accountName   ownerName: $ownerName   type:$type"
                    )
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR])
    fun createCalendar(context: Context, calendarName: String): Long? {
        val values = ContentValues().apply {
            put(CalendarContract.Calendars.ACCOUNT_NAME, calendarName)
            put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
            put(CalendarContract.Calendars.NAME, calendarName)
            put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarName)
            put(CalendarContract.Calendars.CALENDAR_COLOR, Color.RED)
            put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_ROOT
            )
            put(CalendarContract.Calendars.OWNER_ACCOUNT, calendarName)
        }

        val builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendarName)
        builder.appendQueryParameter(
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.ACCOUNT_TYPE_LOCAL
        )
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")

        val uri = context.contentResolver.insert(builder.build(), values)
        Log.w(TAG, "calendar: $uri")

        return uri?.lastPathSegment?.toLong()
    }

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR])
    fun getCalendarId(context: Context, calendarName: String): Long? {
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI

        val projection: Array<String> = arrayOf(
            CalendarContract.Calendars._ID
        )
        val selection: String = "(" +
                "(${CalendarContract.Calendars.ACCOUNT_NAME} = ?) " +
                "AND (${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) " +
                "AND (${CalendarContract.Calendars.CALENDAR_DISPLAY_NAME} = ?)" +
                ")"

        val selectionArgs: Array<String> =
            arrayOf(calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL, calendarName)

        val cur: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cur?.use { cursor ->
            if (cursor.moveToNext()) {
                cursor.getColumnIndex(CalendarContract.Calendars._ID).let { id ->
                    val calendarId = cursor.getLong(id)
                    
                    Log.w(TAG, "calendarId: $calendarId")
                    return calendarId
                }
            }
        }
        return null
    }

    @RequiresPermission(allOf = [Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR])
    fun addEvent(
        context: Context,
        date: Date,
        calendarId: Long,
        eventTitle: String,
        eventDescription: String
    ): Uri? {
        var uri: Uri? = null
        calendarScope.launch {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, date.time)
                put(CalendarContract.Events.DTEND, date.time + 15 * 60 * 1000)
                put(CalendarContract.Events.TITLE, eventTitle)
                put(CalendarContract.Events.DESCRIPTION, eventDescription)
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, "UTC")
            }
            uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }
        return uri
    }
}