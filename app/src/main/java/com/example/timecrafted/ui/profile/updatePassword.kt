package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        updatePasswordBtn.setOnClickListener {
            finish()
        }
        backBtn.setOnClickListener {
            finish()
        }

    }
}