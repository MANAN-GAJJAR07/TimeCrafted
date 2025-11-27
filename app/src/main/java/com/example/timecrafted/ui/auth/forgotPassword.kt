package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository

class forgotPassword : AppCompatActivity() {
    
    private lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        authRepository = AuthRepository()

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val sendRestInstructionsBtn = findViewById<LinearLayout>(R.id.sendRestInstructionsBtn)
        val loginPage = findViewById<TextView>(R.id.loginPage)
        
        // Find email EditText
        val emailEditText = findViewById<EditText>(R.id.r8ilcb8qy4sk)

        backBtn.setOnClickListener {
            finish()
        }
        
        sendRestInstructionsBtn.setOnClickListener {
            val email = emailEditText?.text?.toString()?.trim() ?: ""
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            authRepository.sendPasswordResetEmail(
                email = email,
                onSuccess = {
                    Toast.makeText(this, "Password reset email sent! Check your inbox.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, verifyEmail::class.java))
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        loginPage.setOnClickListener {
            finish()
        }
    }
}
