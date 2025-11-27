package com.example.timecrafted.admin.data.model

data class DashboardStats(
    val totalProducts: Int = 0,
    val totalCustomers: Int = 0,
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val lowStockProducts: Int = 0,
    val recentOrders: List<Order> = emptyList(),
    val monthlyRevenue: List<MonthlyRevenue> = emptyList(),
    val topSellingProducts: List<ProductSales> = emptyList()
) {
    fun getFormattedRevenue(): String = "₹${String.format("%.2f", totalRevenue)}"
}

data class MonthlyRevenue(
    val month: String = "",
    val year: Int = 0,
    val revenue: Double = 0.0
) {
    fun getFormattedRevenue(): String = "₹${String.format("%.2f", revenue)}"
}

data class ProductSales(
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val totalSold: Int = 0,
    val totalRevenue: Double = 0.0
) {
    fun getFormattedRevenue(): String = "₹${String.format("%.2f", totalRevenue)}"
}
