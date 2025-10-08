package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R
import com.example.timecrafted.ui.product.OrderDetailsActivity

class orderHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_history)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val orderDetailsBtn = findViewById<LinearLayout>(R.id.orderDetailsBtn)

        backBtn.setOnClickListener {
            finish()
        }

        orderDetailsBtn.setOnClickListener {
            startActivity(Intent(this, OrderDetailsActivity::class.java))
        }
    }
}