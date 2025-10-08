package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R

class registerScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        val backButton = findViewById<ImageView>(R.id.registerBack)
        val loginPage = findViewById<TextView>(R.id.loginPage)

        backButton.setOnClickListener {
            finish()
        }
        loginPage.setOnClickListener {
            startActivity(Intent(this, loginScreen::class.java))
        }

    }
}