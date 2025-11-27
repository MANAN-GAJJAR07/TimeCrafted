package com.example.timecrafted.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.auth.GoogleSignInHelper
import com.example.timecrafted.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class loginScreen : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val account = GoogleSignInHelper.getSignedInAccountFromIntent(result.data)
                if (account != null) {
                    signInWithGoogle(account)
                } else {
                    Toast.makeText(this, "Google sign in failed: No account returned", Toast.LENGTH_SHORT).show()
                }
            } catch (e: com.google.android.gms.common.api.ApiException) {
                val errorMessage = when (e.statusCode) {
                    com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR -> "Network error. Please check your internet connection."
                    com.google.android.gms.common.api.CommonStatusCodes.INTERNAL_ERROR -> "Internal error. Please try again."
                    com.google.android.gms.common.api.CommonStatusCodes.INVALID_ACCOUNT -> "Invalid account. Please try again."
                    com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_REQUIRED -> "Sign in required."
                    10 -> "Developer error: Please check your Web Client ID configuration."
                    12501 -> "Sign in cancelled by user."
                    7 -> "Network error. Please check your internet connection."
                    else -> "Google sign in failed: ${e.message} (Code: ${e.statusCode})"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                android.util.Log.e("GoogleSignIn", "Sign in error: ${e.statusCode} - ${e.message}", e)
            } catch (e: Exception) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                android.util.Log.e("GoogleSignIn", "Unexpected error", e)
            }
        } else {
            Toast.makeText(this, "Google sign in cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        authRepository = AuthRepository()
        
        // Check if already logged in
        if (authRepository.isUserLoggedIn()) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_login_screen)

        // Initialize Google Sign In
        // Note: Replace with your actual Web Client ID from Firebase Console
        val webClientId = getString(R.string.default_web_client_id)
        if (webClientId.isEmpty() || webClientId == "YOUR_WEB_CLIENT_ID_HERE") {
            Toast.makeText(this, "Google Sign-In not configured. Please set Web Client ID in strings.xml", Toast.LENGTH_LONG).show()
        } else {
            try {
                googleSignInClient = GoogleSignInHelper.createGoogleSignInClient(this, webClientId)
                android.util.Log.d("GoogleSignIn", "Google Sign-In client initialized with Web Client ID: $webClientId")
            } catch (e: Exception) {
                android.util.Log.e("GoogleSignIn", "Failed to initialize Google Sign-In client", e)
                Toast.makeText(this, "Failed to initialize Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        val backLogin = findViewById<ImageView>(R.id.backLogin)
        val googleLogin = findViewById<LinearLayout>(R.id.loginGoogle)
        val login = findViewById<Button>(R.id.btnLogin)
        val emailEdit = findViewById<EditText>(R.id.EmailEditText)
        val passwordEdit = findViewById<EditText>(R.id.PasswordEditText)
        val registerPage = findViewById<TextView>(R.id.registerPage)
        val forgotPasswordBtn = findViewById<TextView>(R.id.forgotPasswordBtn)

        backLogin.setOnClickListener {
            finish()
        }

        registerPage.setOnClickListener {
            startActivity(Intent(this, registerScreen::class.java))
        }

        forgotPasswordBtn.setOnClickListener {
            startActivity(Intent(this, forgotPassword::class.java))
        }

        // Google Login
        googleLogin.setOnClickListener {
            if (::googleSignInClient.isInitialized) {
                val signInIntent = GoogleSignInHelper.getSignInIntent(googleSignInClient)
                googleSignInLauncher.launch(signInIntent)
            } else {
                Toast.makeText(this, "Google Sign-In not configured properly", Toast.LENGTH_LONG).show()
            }
        }

        // Email/Password Login
        login.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signInWithEmailPassword(email, password)
        }
    }

    private fun signInWithEmailPassword(email: String, password: String) {
        authRepository.signInWithEmailPassword(
            email = email,
            password = password,
            onSuccess = { user ->
                Toast.makeText(this, "Welcome back, ${user.email}!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun signInWithGoogle(account: com.google.android.gms.auth.api.signin.GoogleSignInAccount) {
        authRepository.signInWithGoogle(
            account = account,
            onSuccess = { user ->
                // Save user profile to Firebase if not exists
                val userRepository = com.example.timecrafted.data.repository.UserRepository()
                userRepository.getUserProfile(
                    userId = user.uid,
                    onSuccess = { profile ->
                        if (profile == null) {
                            // Create profile for Google sign-in user
                            val userProfile = com.example.timecrafted.data.model.UserProfile(
                                uid = user.uid,
                                name = user.displayName ?: "",
                                email = user.email ?: "",
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            userRepository.saveUserProfile(
                                userId = user.uid,
                                profile = userProfile,
                                onSuccess = {
                                    Toast.makeText(this, "Welcome, ${user.email}!", Toast.LENGTH_SHORT).show()
                                    navigateToMain()
                                },
                                onError = {
                                    Toast.makeText(this, "Welcome, ${user.email}!", Toast.LENGTH_SHORT).show()
                                    navigateToMain()
                                }
                            )
                        } else {
                            Toast.makeText(this, "Welcome, ${user.email}!", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                    },
                    onError = {
                        Toast.makeText(this, "Welcome, ${user.email}!", Toast.LENGTH_SHORT).show()
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
