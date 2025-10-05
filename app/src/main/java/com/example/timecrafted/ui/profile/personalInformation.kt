package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R
import com.example.timecrafted.ui.main.fragment.ProfileFragment

class personalInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)

        val saveChangesBtn = findViewById<Button>(R.id.saveChangesBtn)
        val updatePasswordBtn = findViewById<Button>(R.id.updatePasswordBtn)

        saveChangesBtn.setOnClickListener {
            finish()
        }
        updatePasswordBtn.setOnClickListener {
            val intent = Intent(this, updatePassword::class.java)
            startActivity(intent)
        }


    }
}