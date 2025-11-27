package com.example.timecrafted.admin.data.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val currency: String = "₹",
    val orderStatus: OrderStatus = OrderStatus.pending,
    val orderDate: Long = System.currentTimeMillis(),
    val shippingAddress: Address? = null,
    val paymentMethod: String = "",
    val notes: String = ""
) {
    fun getFormattedTotal(): String = "$currency${String.format("%.2f", total)}"
    
    fun getFormattedDate(): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(orderDate))
    }
}

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val currency: String = "₹"
) {
    fun getSubtotal(): Double = quantity * price
    fun getFormattedSubtotal(): String = "$currency${String.format("%.2f", getSubtotal())}"
}

enum class OrderStatus(val displayName: String) {
    pending("Pending"),
    confirmed("Confirmed"),
    shipped("Shipped"),
    delivered("Delivered"),
    cancelled("Cancelled");
    
    companion object {
        fun fromString(status: String): OrderStatus {
            return values().find { it.name.equals(status, ignoreCase = true) } ?: pending
        }
    }
}

data class Address(
    val fullName: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val phoneNumber: String = ""
)
