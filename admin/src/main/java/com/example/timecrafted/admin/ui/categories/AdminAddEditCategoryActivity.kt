package com.example.timecrafted.admin.ui.categories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.admin.R

class AdminAddEditCategoryActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_edit_category)
        
        supportActionBar?.title = "Add/Edit Category"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // TODO: Implement category add/edit functionality
    }
}
