package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_address_information)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val editAddress = findViewById<LinearLayout>(R.id.editAddress)
        val addAddress = findViewById<LinearLayout>(R.id.addAddress)

        backBtn.setOnClickListener {
            finish()
        }
        editAddress.setOnClickListener {
            startActivity(Intent(this, editAddress::class.java))
        }
        addAddress.setOnClickListener {
            startActivity(Intent(this, addAddress::class.java))
        }
    }
}