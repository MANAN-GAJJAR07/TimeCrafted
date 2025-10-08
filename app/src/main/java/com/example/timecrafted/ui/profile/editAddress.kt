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

class editAddress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_address)

        // Use meaningful variable names based on functionality
        val backArrow = findViewById<ImageView>(R.id.rvdzbgxq6ll)
        val cancelButton = findViewById<Button>(R.id.rj5cn2mp6fh)
        val saveChangesButton = findViewById<Button>(R.id.r3ygx9ogp9xs)
        val deleteAddressButton = findViewById<Button>(R.id.r63e71vxihl)

        backArrow.setOnClickListener {
            finish()
        }
        cancelButton.setOnClickListener {
            finish()
        }
        saveChangesButton.setOnClickListener {
            Toast.makeText(this, "Address updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
        deleteAddressButton.setOnClickListener {
            Toast.makeText(this, "Address deleted successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("EditAddress", "Activity resumed")
        // Ensure activity stays active
    }

    override fun onPause() {
        super.onPause()
        Log.d("EditAddress", "Activity paused")
        // Don't finish here
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("EditAddress", "Activity destroyed")
    }
}