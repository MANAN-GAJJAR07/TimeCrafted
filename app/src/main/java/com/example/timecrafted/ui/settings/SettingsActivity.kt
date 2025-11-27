package com.example.timecrafted.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.timecrafted.R
import com.example.timecrafted.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("app_settings", MODE_PRIVATE)

        setupToolbar()
        setupAccountSection()
        setupNotificationsSection()
        setupAppPreferencesSection()
        setupHelpSection()
        setupAboutSection()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAccountSection() {
        binding.linkedAccounts.setOnClickListener {
            // Navigate to linked accounts
        }

        binding.privacySettings.setOnClickListener {
            // Navigate to privacy settings
        }
    }

    private fun setupNotificationsSection() {
        // Load saved preferences
        binding.switchPromotions.isChecked = prefs.getBoolean("notifications_promotions", false)
        binding.switchOrderUpdates.isChecked = prefs.getBoolean("notifications_order_updates", false)
        binding.switchReviewReminders.isChecked = prefs.getBoolean("notifications_review_reminders", false)

        binding.switchPromotions.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications_promotions", isChecked).apply()
        }

        binding.switchOrderUpdates.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications_order_updates", isChecked).apply()
        }

        binding.switchReviewReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications_review_reminders", isChecked).apply()
        }
    }

    private fun setupAppPreferencesSection() {
        // Language
        binding.languageValue.text = prefs.getString("language", "English") ?: "English"
        binding.language.setOnClickListener {
            // Show language selection dialog
        }

        // Theme
        val currentTheme = prefs.getString("theme", "system") ?: "system"
        updateThemeUI(currentTheme)
        binding.theme.setOnClickListener {
            showThemeSelectionDialog()
        }

        // Measurement Units
        binding.measurementUnitsValue.text = prefs.getString("measurement_units", "Metric") ?: "Metric"
        binding.measurementUnits.setOnClickListener {
            // Show measurement units selection dialog
        }
    }

    private fun updateThemeUI(theme: String) {
        binding.themeValue.text = when (theme) {
            "light" -> "Light"
            "dark" -> "Dark"
            else -> "System"
        }
    }

    private fun showThemeSelectionDialog() {
        val themes = arrayOf("Light", "Dark", "System")
        val currentTheme = prefs.getString("theme", "system") ?: "system"
        val currentIndex = when (currentTheme) {
            "light" -> 0
            "dark" -> 1
            else -> 2
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select Theme")
            .setSingleChoiceItems(themes, currentIndex) { dialog, which ->
                val selectedTheme = when (which) {
                    0 -> "light"
                    1 -> "dark"
                    else -> "system"
                }
                prefs.edit().putString("theme", selectedTheme).apply()
                applyTheme(selectedTheme)
                updateThemeUI(selectedTheme)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyTheme(theme: String) {
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setupHelpSection() {
        binding.faq.setOnClickListener {
            // Navigate to FAQ
        }

        binding.contactSupport.setOnClickListener {
            // Navigate to contact support
        }
    }

    private fun setupAboutSection() {
        binding.versionValue.text = "1.2.3"
        binding.privacyPolicy.setOnClickListener {
            // Navigate to privacy policy
        }
        binding.termsOfService.setOnClickListener {
            // Navigate to terms of service
        }
    }

}

