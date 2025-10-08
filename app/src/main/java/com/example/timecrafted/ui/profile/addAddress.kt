package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_address)

        val cancelBtn = findViewById<Button>(R.id.cancelBtn)
        val saveAddressBtn = findViewById<Button>(R.id.saveAddressBtn)
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        cancelBtn.setOnClickListener {
            startActivity(Intent(this, addressInformation::class.java))
            finish()
        }
        saveAddressBtn.setOnClickListener {
            Toast.makeText(this, "Address Save successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, addressInformation::class.java))
            finish()
        }
        backBtn.setOnClickListener {
            finish()
        }

    }
}