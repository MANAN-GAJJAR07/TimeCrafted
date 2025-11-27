package com.example.timecrafted.admin.ui.categories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.admin.databinding.ActivityAdminListBinding

class AdminCategoriesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminListBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Manage Categories"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // TODO: Implement categories management
    }
}
