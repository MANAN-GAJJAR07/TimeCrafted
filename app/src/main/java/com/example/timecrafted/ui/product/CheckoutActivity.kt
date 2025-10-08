package com.example.timecrafted.ui.product


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val btnPlaceOrder: Button = findViewById(R.id.btnPlaceOrder)
        btnPlaceOrder.setOnClickListener {
            Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, OrderConfirmationActivity::class.java))
            finish()
        }
    }
}
