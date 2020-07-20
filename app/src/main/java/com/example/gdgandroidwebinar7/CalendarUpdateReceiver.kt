package com.example.gdgandroidwebinar7

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

class CalendarUpdateReceiver: BroadcastReceiver() {
    private val TAG = CalendarUpdateReceiver::class.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            Toast.makeText(context, "Calendar updated", Toast.LENGTH_SHORT).show()

            val serviceIntent = Intent(it.applicationContext, CalendarUpdateService::class.java)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                it.startForegroundService(serviceIntent)
            else
                it.startService(serviceIntent)
        }
        Log.w(TAG, "event received")

    }
}