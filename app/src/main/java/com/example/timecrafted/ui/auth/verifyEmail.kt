package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R

class verifyEmail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnVerify = findViewById<Button>(R.id.btnVerify)

        btnBack.setOnClickListener {
            finish()
        }
        btnVerify.setOnClickListener {
            startActivity(Intent(this, setNewPassword::class.java))
        }

    }
}