package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R

class addressInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_information)

        // Use meaningful IDs based on actual layout IDs
        val backArrow = findViewById<ImageView>(R.id.backBtn)
        val editAddressContainer = findViewById<LinearLayout>(R.id.editAddress)
        val addAddressContainer = findViewById<LinearLayout>(R.id.addAddress)

        backArrow.setOnClickListener {
            finish()
        }
        editAddressContainer.setOnClickListener {
            val intent = Intent(this, editAddress::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        addAddressContainer.setOnClickListener {
            val intent = Intent(this, addAddress::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("AddressInfo", "Activity resumed")
        // Ensure activity stays active
    }

    override fun onPause() {
        super.onPause()
        Log.d("AddressInfo", "Activity paused")
        // Don't finish here
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AddressInfo", "Activity destroyed")
    }
}