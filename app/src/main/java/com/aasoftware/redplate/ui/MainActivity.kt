package com.aasoftware.redplate.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aasoftware.redplate.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // TODO: Check if user is still logged in
    }
}