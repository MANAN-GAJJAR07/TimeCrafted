package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.model.Order
import com.example.timecrafted.data.repository.OrderRepository
import com.example.timecrafted.ui.product.OrderDetailsActivity
import com.example.timecrafted.ui.product.adapter.OrderHistoryAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ValueEventListener

class orderHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var adapter: OrderHistoryAdapter
    private val authRepository by lazy { AuthRepository() }
    private val orderRepository by lazy { OrderRepository() }
    private var orderListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            finish()
            return
        }

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        recyclerView = findViewById(R.id.rvOrderHistory)
        emptyState = findViewById(R.id.emptyState)

        backBtn.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadOrders(userId)
    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter(
            onOrderClick = { order ->
                val intent = Intent(this, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", order.id)
                startActivity(intent)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadOrders(userId: String) {
        orderListener = orderRepository.getUserOrders(
            userId = userId,
            onResult = { orders ->
                adapter.submitList(orders)
                emptyState.isVisible = orders.isEmpty()
                recyclerView.isVisible = orders.isNotEmpty()
            },
            onError = { error ->
                Snackbar.make(findViewById(android.R.id.content), "Failed to load orders: $error", Snackbar.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        orderListener?.let { listener ->
            orderRepository.removeOrderListener(listener)
        }
    }
}