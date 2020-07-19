package com.example.gdgandroidwebinar7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var hasPeriodicUpdates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        periodicUpdateSwitch.setOnCheckedChangeListener { _, isChecked ->
            hasPeriodicUpdates = isChecked
        }

        addCalendarButton.setOnClickListener {
            //TODO
        }

        addEventButton.setOnClickListener {
            //TODO
        }
    }
}
