package com.example.timecrafted.admin.ui.users

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.admin.R

class AdminAddEditUserActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_edit_user)
        
        supportActionBar?.title = "Add/Edit User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // TODO: Implement user add/edit functionality
    }
}
