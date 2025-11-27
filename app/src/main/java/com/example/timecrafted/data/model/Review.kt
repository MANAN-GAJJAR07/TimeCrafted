package com.example.timecrafted.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Review(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val userId: String = "",
    val orderId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

