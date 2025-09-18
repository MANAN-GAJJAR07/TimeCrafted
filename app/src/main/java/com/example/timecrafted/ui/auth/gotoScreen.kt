package com.example.timecrafted.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R

class gotoScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_goto_screen)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnContinueAsGuest = findViewById<Button>(R.id.btnContinueAsGuest)



        btnRegister.setOnClickListener {
            startActivity(Intent(this, registerScreen::class.java))
        }
        btnLogin.setOnClickListener {
            startActivity(Intent(this, loginScreen::class.java))
        }
        btnContinueAsGuest.setOnClickListener {
            startActivity(Intent(this, registerScreen::class.java))
        }


    }
}