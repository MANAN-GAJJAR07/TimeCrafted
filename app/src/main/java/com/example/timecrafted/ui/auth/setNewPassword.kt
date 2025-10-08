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

class setNewPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_password)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSetPassword = findViewById<Button>(R.id.btnSetPassword)

        btnBack.setOnClickListener {
            finish()
        }
        btnSetPassword.setOnClickListener {
            startActivity(Intent(this, passwordChangeSuccess::class.java))
        }

    }
}