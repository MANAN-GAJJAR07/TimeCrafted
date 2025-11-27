package com.example.timecrafted.ui.profile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class updatePassword : AppCompatActivity() {

    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var confirmNewPasswordInput: EditText
    private lateinit var updatePasswordBtn: Button
    private lateinit var backBtn: ImageView

    private val authRepository by lazy { AuthRepository() }
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        val user = firebaseAuth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Check if user has email/password provider
        val hasEmailPassword = user.providerData.any { it.providerId == "password" }
        if (!hasEmailPassword) {
            Toast.makeText(this, "Password update is only available for email/password accounts", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentPasswordInput = findViewById(R.id.currentPasswordInput)
        newPasswordInput = findViewById(R.id.newPasswordInput)
        confirmNewPasswordInput = findViewById(R.id.confirmNewPasswordInput)
        updatePasswordBtn = findViewById(R.id.updatePasswordButton)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            finish()
        }

        updatePasswordBtn.setOnClickListener {
            if (validateInput()) {
                updatePassword(user.email!!)
            }
        }
    }

    private fun validateInput(): Boolean {
        val currentPassword = currentPasswordInput.text.toString().trim()
        val newPassword = newPasswordInput.text.toString().trim()
        val confirmPassword = confirmNewPasswordInput.text.toString().trim()

        if (currentPassword.isEmpty()) {
            currentPasswordInput.error = "Please enter current password"
            return false
        }

        if (newPassword.isEmpty()) {
            newPasswordInput.error = "Please enter new password"
            return false
        }

        if (newPassword.length < 6) {
            newPasswordInput.error = "Password must be at least 6 characters"
            return false
        }

        if (newPassword != confirmPassword) {
            confirmNewPasswordInput.error = "Passwords do not match"
            return false
        }

        if (currentPassword == newPassword) {
            newPasswordInput.error = "New password must be different from current password"
            return false
        }

        return true
    }

    private fun updatePassword(email: String) {
        val currentPassword = currentPasswordInput.text.toString().trim()
        val newPassword = newPasswordInput.text.toString().trim()

        updatePasswordBtn.isEnabled = false
        updatePasswordBtn.text = "Updating..."

        // Re-authenticate user
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        firebaseAuth.currentUser?.reauthenticate(credential)
            ?.addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // Update password
                    firebaseAuth.currentUser?.updatePassword(newPassword)
                        ?.addOnCompleteListener { updateTask ->
                            updatePasswordBtn.isEnabled = true
                            updatePasswordBtn.text = "Update Password"
                            
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                val error = updateTask.exception?.message ?: "Failed to update password"
                                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    updatePasswordBtn.isEnabled = true
                    updatePasswordBtn.text = "Update Password"
                    val error = reauthTask.exception?.message ?: "Authentication failed"
                    if (error.contains("wrong-password", ignoreCase = true) || 
                        error.contains("invalid-credential", ignoreCase = true)) {
                        currentPasswordInput.error = "Incorrect current password"
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
    }
}