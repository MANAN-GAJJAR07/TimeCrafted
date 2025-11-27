package com.example.timecrafted.admin.ui.products

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.admin.R

class AdminAddEditProductActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_edit_product)
        
        val productId = intent.getStringExtra("product_id")
        val isEdit = productId != null
        
        supportActionBar?.title = if (isEdit) "Edit Product" else "Add Product"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // TODO: Implement product add/edit functionality
    }
}
