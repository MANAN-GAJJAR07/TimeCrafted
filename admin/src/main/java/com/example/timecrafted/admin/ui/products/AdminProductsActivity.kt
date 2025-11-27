package com.example.timecrafted.admin.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.admin.R
import com.example.timecrafted.admin.data.model.Product
import com.example.timecrafted.admin.data.repository.AdminRepository
import com.example.timecrafted.admin.databinding.ActivityAdminListBinding
import com.example.timecrafted.admin.ui.adapter.AdminProductAdapter

class AdminProductsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminListBinding
    private val adminRepository = AdminRepository()
    private lateinit var productAdapter: AdminProductAdapter
    private var allProducts = listOf<Product>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.manage_products)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupRecyclerView()
        loadProducts()
        
        // Check if we need to filter for low stock products
        val filter = intent.getStringExtra("filter")
        if (filter == "low_stock") {
            supportActionBar?.title = "Low Stock Products"
        }
    }
    
    private fun setupRecyclerView() {
        productAdapter = AdminProductAdapter(
            onEditClick = { product -> editProduct(product) },
            onDeleteClick = { product -> deleteProduct(product) }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminProductsActivity)
            adapter = productAdapter
        }
    }
    
    private fun loadProducts() {
        adminRepository.getProducts { products ->
            allProducts = products
            
            val filter = intent.getStringExtra("filter")
            val filteredProducts = if (filter == "low_stock") {
                products.filter { it.isLowStock() }
            } else {
                products
            }
            
            productAdapter.updateProducts(filteredProducts)
        }
    }
    
    private fun editProduct(product: Product) {
        val intent = Intent(this, AdminAddEditProductActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }
    
    private fun deleteProduct(product: Product) {
        // Show confirmation dialog and delete
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Delete") { _, _ ->
                // Delete product logic here
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_list_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_add -> {
                val intent = Intent(this, AdminAddEditProductActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
