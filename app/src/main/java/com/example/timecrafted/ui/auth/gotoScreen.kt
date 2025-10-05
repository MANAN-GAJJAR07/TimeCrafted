package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.ui.main.MainActivity

class gotoScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🧠 Step 1: Check login status from SharedPreferences
        val sharedPref = getSharedPreferences("LoginPref", MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", null)

        // 🧩 Step 2: If already logged in, go directly to MainActivity
        if (savedEmail != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // 🧩 Step 3: Otherwise, show this screen
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
            // You can either go to MainActivity directly or a limited guest mode
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
