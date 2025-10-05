package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.ui.main.MainActivity

class loginScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🧠 Step 1: Check if already logged in
        val sharedPref = getSharedPreferences("LoginPref", MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", null)

        if (savedEmail != null) {
            // Already logged in -> go to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", savedEmail)
            startActivity(intent)
            finish()
            return
        }

        // 🧠 Step 2: Normal login screen
        setContentView(R.layout.activity_login_screen)

        val backLogin = findViewById<ImageView>(R.id.backLogin)
        val login = findViewById<Button>(R.id.btnLogin)
        val emailEdit = findViewById<EditText>(R.id.EmailEditText)
        val passwordEdit = findViewById<EditText>(R.id.PasswordEditText)

        backLogin.setOnClickListener {
            startActivity(Intent(this, gotoScreen::class.java))
            finish()
        }

        login.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            // 🧠 Step 3: Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🧠 Step 4: Check credentials
            if (email == "Soham" && password == "12345") {
                // Save login state
                val editor = sharedPref.edit()
                editor.putString("email", email)
                editor.apply()

                // Navigate to main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials. Try again!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
