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

class passwordChangeSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change_success)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val backToLoginBtn = findViewById<Button>(R.id.backToLoginBtn)

        btnBack.setOnClickListener {
            finish()
        }
        backToLoginBtn.setOnClickListener {
            startActivity(Intent(this, loginScreen::class.java))
        }
    }
}