package com.example.timecrafted.ui.product

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.R
import com.example.timecrafted.data.repository.OrderRepository
import com.example.timecrafted.ui.main.MainActivity
import com.example.timecrafted.ui.product.adapter.OrderItemAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var tvOrderNumber: TextView
    private lateinit var tvSubtotalValue: TextView
    private lateinit var tvShippingValue: TextView
    private lateinit var tvPlatformFee: TextView
    private lateinit var tvPlatformFeeValue: TextView
    private lateinit var tvTotalValue: TextView
    private lateinit var tvShippingAddress: TextView
    private lateinit var tvPaymentDetails: TextView
    private lateinit var itemsRecyclerView: RecyclerView
    private val orderItemsAdapter by lazy { OrderItemAdapter() }

    private val orderRepository by lazy { OrderRepository() }
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        orderId = intent.getStringExtra("orderId") ?: ""
        if (orderId.isEmpty()) {
            finish()
            return
        }

        // Initialize views
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val continueBrowsingBtn = findViewById<Button>(R.id.continueBrowsingBtn)
        tvOrderNumber = findViewById(R.id.tvOrderNumber)
        tvSubtotalValue = findViewById(R.id.tvSubtotalValue)
        tvShippingValue = findViewById(R.id.tvShippingValue)
        tvPlatformFee = findViewById(R.id.tvPlatformFee)
        tvPlatformFeeValue = findViewById(R.id.tvPlatformFeeValue)
        tvTotalValue = findViewById(R.id.tvTotalValue)
        tvShippingAddress = findViewById(R.id.tvShippingAddress)
        tvPaymentDetails = findViewById(R.id.tvPaymentDetails)
        itemsRecyclerView = findViewById(R.id.recyclerOrderItems)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        itemsRecyclerView.adapter = orderItemsAdapter

        backBtn.setOnClickListener {
            finish()
        }

        continueBrowsingBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        // Get order from Firebase
        orderRepository.getOrderById(
            orderId = orderId,
            onSuccess = { order ->
                displayOrder(order)
            },
            onError = { error ->
                // Handle error
            }
        )
    }

    private fun displayOrder(order: com.example.timecrafted.data.model.Order) {
        tvOrderNumber.text = order.id.take(8)

        // Display all items
        orderItemsAdapter.submitList(order.items)

        // Format currency
        val format = NumberFormat.getNumberInstance(Locale.getDefault())
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 2
        val currency = order.currency

        tvSubtotalValue.text = "$currency${format.format(order.subtotal)}"
        tvShippingValue.text = if (order.shipping == 0.0) "Free" else "$currency${format.format(order.shipping)}"
        
        // Platform fee (only for COD)
        if (order.platformFee > 0) {
            tvPlatformFee.visibility = android.view.View.VISIBLE
            tvPlatformFeeValue.visibility = android.view.View.VISIBLE
            tvPlatformFeeValue.text = "$currency${format.format(order.platformFee)}"
        } else {
            tvPlatformFee.visibility = android.view.View.GONE
            tvPlatformFeeValue.visibility = android.view.View.GONE
        }
        
        tvTotalValue.text = "$currency${format.format(order.total)}"

        // Shipping address
        order.shippingAddress?.let { address ->
            val addressText = buildString {
                append(address.fullName)
                if (address.phoneNumber.isNotEmpty()) {
                    append("\nPhone: ${address.phoneNumber}")
                }
                append("\n${address.addressLine1}")
                if (address.addressLine2.isNotEmpty()) {
                    append(", ${address.addressLine2}")
                }
                append("\n${address.city}, ${address.state} ${address.zipCode}")
                if (address.country.isNotEmpty()) {
                    append(", ${address.country}")
                }
            }
            tvShippingAddress.text = addressText
        }

        // Payment method
        val paymentText = when (order.paymentMethod) {
            "cod" -> "Cash on Delivery"
            "razorpay" -> "Razorpay (Online Payment)"
            else -> order.paymentMethod
        }
        tvPaymentDetails.text = paymentText

        // Estimated delivery (7-10 days from order date)
        val deliveryDate = Date(order.createdAt + (7 * 24 * 60 * 60 * 1000L))
        val deliveryDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val deliveryDateText = deliveryDateFormat.format(deliveryDate)
        val deliveryDateEnd = Date(order.createdAt + (10 * 24 * 60 * 60 * 1000L))
        val deliveryDateEndText = deliveryDateFormat.format(deliveryDateEnd)
        findViewById<TextView>(R.id.tvPaymentDetails).text = "$deliveryDateText - $deliveryDateEndText"
        
        // Update payment method label
        findViewById<TextView>(R.id.tvPaymentMethod).text = "Estimated Delivery"
    }
}