package com.example.timecrafted.admin.data.model

data class Category(
    val id: String = "",
    val name: String = "",
    val imagePath: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val productCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
