package com.example.timecrafted.admin.ui.orders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.admin.R

class AdminOrderDetailsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order_details)
        
        supportActionBar?.title = "Order Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // TODO: Implement order details functionality
    }
}
