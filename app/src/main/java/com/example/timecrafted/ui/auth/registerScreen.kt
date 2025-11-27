package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.model.UserProfile
import com.example.timecrafted.data.repository.UserRepository
import com.example.timecrafted.ui.main.MainActivity

class registerScreen : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        authRepository = AuthRepository()
        userRepository = UserRepository()

        val backButton = findViewById<ImageView>(R.id.registerBack)
        val loginPage = findViewById<TextView>(R.id.loginPage)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val fullNameEdit = findViewById<EditText>(R.id.rgh2sfydx8s7)
        val emailEdit = findViewById<EditText>(R.id.rp0bu1oqe2ca)
        val passwordEdit = findViewById<EditText>(R.id.r71sb4nktogh)

        backButton.setOnClickListener {
            finish()
        }

        loginPage.setOnClickListener {
            startActivity(Intent(this, loginScreen::class.java))
        }

        btnRegister.setOnClickListener {
            val fullName = fullNameEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerWithEmailPassword(email, password, fullName)
        }
    }

    private fun registerWithEmailPassword(email: String, password: String, fullName: String) {
        authRepository.signUpWithEmailPassword(
            email = email,
            password = password,
            onSuccess = { user ->
                // Save user profile to Firebase Realtime Database
                val userProfile = UserProfile(
                    uid = user.uid,
                    name = fullName,
                    email = email,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                userRepository.saveUserProfile(
                    userId = user.uid,
                    profile = userProfile,
                    onSuccess = {
                        Toast.makeText(this, "Registration successful! Welcome, $fullName!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    },
                    onError = { error ->
                        Toast.makeText(this, "Registration successful but failed to save profile: $error", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                )
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
