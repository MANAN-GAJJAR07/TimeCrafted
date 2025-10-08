package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R
import kotlin.math.log

class forgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val sendRestInstructionsBtn = findViewById<LinearLayout>(R.id.sendRestInstructionsBtn)
        val loginPage = findViewById<TextView>(R.id.loginPage)

        backBtn.setOnClickListener {
            finish()
        }
        sendRestInstructionsBtn.setOnClickListener {
            startActivity(Intent(this, verifyEmail::class.java))
        }
        loginPage.setOnClickListener {
            finish()
        }

    }
}