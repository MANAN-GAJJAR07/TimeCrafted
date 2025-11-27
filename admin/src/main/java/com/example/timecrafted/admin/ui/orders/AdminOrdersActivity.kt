package com.example.timecrafted.admin.ui.orders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.admin.databinding.ActivityAdminListBinding

class AdminOrdersActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminListBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Manage Orders"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // TODO: Implement orders management
    }
}
