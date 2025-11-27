package com.example.timecrafted.admin.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.timecrafted.admin.R
import com.example.timecrafted.admin.data.repository.AdminRepository
import com.example.timecrafted.admin.databinding.ActivityAdminLoginBinding
import com.example.timecrafted.admin.ui.dashboard.AdminDashboardActivity
import kotlinx.coroutines.launch

class AdminLoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminLoginBinding
    private val adminRepository = AdminRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Check if admin is already logged in
        if (adminRepository.getCurrentAdminId() != null) {
            navigateToDashboard()
            return
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnAdminLogin.setOnClickListener {
            loginAdmin()
        }
    }
    
    private fun loginAdmin() {
        val email = binding.etAdminEmail.text.toString().trim()
        val password = binding.etAdminPassword.text.toString().trim()
        
        // Validate input
        if (email.isEmpty()) {
            binding.emailLayout.error = "Email is required"
            return
        }
        
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            return
        }
        
        // Clear previous errors
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        
        // Show loading
        showLoading(true)
        
        lifecycleScope.launch {
            val result = adminRepository.loginAdmin(email, password)
            
            showLoading(false)
            
            if (result.isSuccess) {
                Toast.makeText(this@AdminLoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToDashboard()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Login failed"
                Toast.makeText(this@AdminLoginActivity, error, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnAdminLogin.isEnabled = !show
        binding.etAdminEmail.isEnabled = !show
        binding.etAdminPassword.isEnabled = !show
    }
    
    private fun navigateToDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
