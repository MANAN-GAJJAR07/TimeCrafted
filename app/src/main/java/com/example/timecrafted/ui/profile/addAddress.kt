package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R

class addAddress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        // Use meaningful variable names based on functionality
        val cancelButton = findViewById<Button>(R.id.cancelBtn)
        val saveAddressButton = findViewById<Button>(R.id.saveAddressBtn)
        val backArrow = findViewById<ImageView>(R.id.backBtn)

        cancelButton.setOnClickListener {
            finish()
        }
        saveAddressButton.setOnClickListener {
            Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
        backArrow.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("AddAddress", "Activity resumed")
        // Ensure activity stays active
    }

    override fun onPause() {
        super.onPause()
        Log.d("AddAddress", "Activity paused")
        // Don't finish here
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AddAddress", "Activity destroyed")
    }
}