package com.example.timecrafted.ui.product

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R
import com.example.timecrafted.ui.main.MainActivity
import com.example.timecrafted.ui.main.fragment.HomeFragment
import com.example.timecrafted.ui.main.fragment.ShopFragment

class OrderConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val continueBrowsingBtn = findViewById<Button>(R.id.continueBrowsingBtn)

        backBtn.setOnClickListener {
            finish()
        }
        continueBrowsingBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }
    }
}