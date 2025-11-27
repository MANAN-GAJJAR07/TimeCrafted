package com.example.timecrafted.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val taxes: Double = 0.0,
    val platformFee: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "₹",
    val paymentMethod: String = "", // "razorpay" or "cod"
    val paymentStatus: String = "", // "pending", "paid", "failed"
    val orderStatus: String = "pending", // "pending", "confirmed", "shipped", "delivered", "cancelled"
    val shippingAddress: Address? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class OrderItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imagePath: String = "",
    val currency: String = "₹"
)

