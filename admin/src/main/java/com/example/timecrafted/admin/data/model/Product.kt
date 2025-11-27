package com.example.timecrafted.admin.data.model

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val currency: String = "₹",
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    val style: String = "",
    val bandMaterial: String = "",
    val caseSize: String = "",
    val imagePath: String = "",
    val stock: Int = 0,
    val lowStockThreshold: Int = 10,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isLowStock(): Boolean = stock <= lowStockThreshold
    
    fun getFormattedPrice(): String = "$currency${String.format("%.2f", price)}"
}
