package com.example.timecrafted.admin.data.model

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val isActive: Boolean = true,
    val registrationDate: Long = System.currentTimeMillis(),
    val lastLoginDate: Long = 0L,
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0
) {
    fun getFormattedRegistrationDate(): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(registrationDate))
    }
    
    fun getFormattedTotalSpent(): String = "₹${String.format("%.2f", totalSpent)}"
}
