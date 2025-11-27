package com.example.timecrafted.admin.ui.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.admin.R

class AdminStatisticsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_statistics)
        
        supportActionBar?.title = "Statistics"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // TODO: Implement statistics with charts
    }
}
