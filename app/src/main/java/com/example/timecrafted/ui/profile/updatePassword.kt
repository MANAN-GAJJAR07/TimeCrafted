package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R

class updatePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        val updatePasswordBtn = findViewById<Button>(R.id.updatePasswordButton)
        val backBtn = findViewById<Button>(R.id.backBtn)

        updatePasswordBtn.setOnClickListener {
            startActivity(Intent(this, personalInformation::class.java))
        }
        backBtn.setOnClickListener {
            finish()
        }

    }
}