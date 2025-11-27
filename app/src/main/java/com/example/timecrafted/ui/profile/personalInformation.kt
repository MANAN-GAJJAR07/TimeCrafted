package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.repository.UserRepository
import com.google.android.material.snackbar.Snackbar

class personalInformation : AppCompatActivity() {

    private lateinit var nameEdit: EditText
//    private lateinit var emailEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var saveChangesBtn: Button
    private lateinit var updatePasswordBtn: Button
    private lateinit var backBtn: ImageView

    private val authRepository by lazy { AuthRepository() }
    private val userRepository by lazy { UserRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        backBtn = findViewById(R.id.ryu0tbjdkghc)
        nameEdit = findViewById(R.id.nameEdit)
//        emailEdit = findViewById(R.id.emailEdit)
        phoneNumberEdit = findViewById(R.id.phoneNumberEdit)
        saveChangesBtn = findViewById(R.id.saveChangesBtn)
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn)

        backBtn.setOnClickListener {
            finish()
        }

        saveChangesBtn.setOnClickListener {
            if (validateInput()) {
                saveUserProfile(userId)
            }
        }

        updatePasswordBtn.setOnClickListener {
            val intent = Intent(this, updatePassword::class.java)
            startActivity(intent)
        }

        // Load user profile data
        loadUserProfile(userId)
    }

    private fun loadUserProfile(userId: String) {
        userRepository.getUserProfile(
            userId = userId,
            onSuccess = { profile ->
                profile?.let {
                    nameEdit.setText(it.name)
//                    emailEdit.setText(it.email)
                    phoneNumberEdit.setText(it.phoneNumber)
                } ?: run {
                    // No profile found, use auth data
                    val user = authRepository.getCurrentUser()
                    nameEdit.setText(user?.displayName ?: "")
//                    emailEdit.setText(user?.email ?: "")
                }
            },
            onError = {
                // On error, use auth data
                val user = authRepository.getCurrentUser()
                nameEdit.setText(user?.displayName ?: "")
//                emailEdit.setText(user?.email ?: "")
            }
        )
    }

    private fun validateInput(): Boolean {
        if (nameEdit.text.toString().trim().isEmpty()) {
            nameEdit.error = "Please enter your name"
            return false
        }
//        if (emailEdit.text.toString().trim().isEmpty()) {
//            emailEdit.error = "Please enter your email"
//            return false
//        }
        return true
    }

    private fun saveUserProfile(userId: String) {
        val updates = mutableMapOf<String, Any>()
        
        val name = nameEdit.text.toString().trim()
//        val email = emailEdit.text.toString().trim()
        val phoneNumber = phoneNumberEdit.text.toString().trim()

        if (name.isNotEmpty()) {
            updates["name"] = name
        }
//        if (email.isNotEmpty()) {
//            updates["email"] = email
//        }
        if (phoneNumber.isNotEmpty()) {
            updates["phoneNumber"] = phoneNumber
        }

        if (updates.isEmpty()) {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show()
            return
        }

        userRepository.updateUserProfile(
            userId = userId,
            updates = updates,
            onSuccess = {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                Snackbar.make(findViewById(android.R.id.content), "Failed to update: $error", Snackbar.LENGTH_SHORT).show()
            }
        )
    }
}