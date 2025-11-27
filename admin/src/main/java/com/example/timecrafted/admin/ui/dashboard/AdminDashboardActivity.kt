package com.example.timecrafted.admin.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.admin.R
import com.example.timecrafted.admin.data.model.DashboardStats
import com.example.timecrafted.admin.data.repository.AdminRepository
import com.example.timecrafted.admin.databinding.ActivityAdminDashboardBinding
import com.example.timecrafted.admin.ui.adapter.RecentOrdersAdapter
import com.example.timecrafted.admin.ui.auth.AdminLoginActivity
import com.example.timecrafted.admin.ui.categories.AdminCategoriesActivity
import com.example.timecrafted.admin.ui.orders.AdminOrdersActivity
import com.example.timecrafted.admin.ui.products.AdminProductsActivity
import com.example.timecrafted.admin.ui.statistics.AdminStatisticsActivity
import com.example.timecrafted.admin.ui.users.AdminUsersActivity
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminDashboardBinding
    private val adminRepository = AdminRepository()
    private lateinit var recentOrdersAdapter: RecentOrdersAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.admin_dashboard)
        
        setupRecyclerView()
        setupClickListeners()
        loadDashboardStats()
    }
    
    override fun onResume() {
        super.onResume()
        loadDashboardStats() // Refresh stats when returning to dashboard
    }
    
    private fun setupRecyclerView() {
        recentOrdersAdapter = RecentOrdersAdapter { order ->
            // Navigate to order details
            val intent = Intent(this, AdminOrdersActivity::class.java)
            intent.putExtra("orderId", order.id)
            startActivity(intent)
        }
        
        binding.recyclerRecentOrders.apply {
            layoutManager = LinearLayoutManager(this@AdminDashboardActivity)
            adapter = recentOrdersAdapter
            isNestedScrollingEnabled = false
        }
    }
    
    private fun setupClickListeners() {
        // Statistics cards click listeners
        binding.cardTotalProducts.setOnClickListener {
            startActivity(Intent(this, AdminProductsActivity::class.java))
        }
        
        binding.cardTotalCustomers.setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }
        
        binding.cardTotalOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }
        
        binding.cardTotalRevenue.setOnClickListener {
            startActivity(Intent(this, AdminStatisticsActivity::class.java))
        }
        
        // Low stock alert
        binding.cardLowStock.setOnClickListener {
            val intent = Intent(this, AdminProductsActivity::class.java)
            intent.putExtra("filter", "low_stock")
            startActivity(intent)
        }
        
        binding.tvViewLowStock.setOnClickListener {
            val intent = Intent(this, AdminProductsActivity::class.java)
            intent.putExtra("filter", "low_stock")
            startActivity(intent)
        }
        
        // Recent orders
        binding.tvViewAllOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }
        
        // Management cards
        binding.cardManageProducts.setOnClickListener {
            startActivity(Intent(this, AdminProductsActivity::class.java))
        }
        
        binding.cardManageOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }
        
        binding.cardManageUsers.setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }
        
        binding.cardManageCategories.setOnClickListener {
            startActivity(Intent(this, AdminCategoriesActivity::class.java))
        }
        
        // Statistics button
        binding.btnViewStatistics.setOnClickListener {
            startActivity(Intent(this, AdminStatisticsActivity::class.java))
        }
    }
    
    private fun loadDashboardStats() {
        lifecycleScope.launch {
            val result = adminRepository.getDashboardStats()
            
            if (result.isSuccess) {
                val stats = result.getOrNull()
                stats?.let { updateUI(it) }
            } else {
                // Handle error - could show a toast or error state
            }
        }
    }
    
    private fun updateUI(stats: DashboardStats) {
        // Update statistics cards
        binding.tvTotalProducts.text = stats.totalProducts.toString()
        binding.tvTotalCustomers.text = stats.totalCustomers.toString()
        binding.tvTotalOrders.text = stats.totalOrders.toString()
        binding.tvTotalRevenue.text = stats.getFormattedRevenue()
        
        // Update low stock alert
        binding.tvLowStockCount.text = if (stats.lowStockProducts > 0) {
            "${stats.lowStockProducts} products need restocking"
        } else {
            "All products are well stocked"
        }
        
        // Update recent orders
        recentOrdersAdapter.updateOrders(stats.recentOrders)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_dashboard_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                loadDashboardStats()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        adminRepository.logout()
        val intent = Intent(this, AdminLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
