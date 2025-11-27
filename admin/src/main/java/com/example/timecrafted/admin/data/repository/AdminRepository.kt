package com.example.timecrafted.admin.data.repository

import com.example.timecrafted.admin.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdminRepository {
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    // Authentication
    suspend fun loginAdmin(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentAdminId(): String? = auth.currentUser?.uid

    // Dashboard Statistics
    suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            val totalProducts = getProductsCount()
            val totalCustomers = getUsersCount()
            val totalOrders = getOrdersCount()
            val totalRevenue = getTotalRevenue()
            val lowStockProducts = getLowStockProductsCount()
            val recentOrders = getRecentOrders(5)

            val stats = DashboardStats(
                totalProducts = totalProducts,
                totalCustomers = totalCustomers,
                totalOrders = totalOrders,
                totalRevenue = totalRevenue,
                lowStockProducts = lowStockProducts,
                recentOrders = recentOrders
            )
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getProductsCount(): Int {
        return suspendCoroutine { continuation ->
            database.child("collections").child("shop")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot.childrenCount.toInt())
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(0)
                    }
                })
        }
    }

    private suspend fun getUsersCount(): Int {
        return suspendCoroutine { continuation ->
            database.child("users")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot.childrenCount.toInt())
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(0)
                    }
                })
        }
    }

    private suspend fun getOrdersCount(): Int {
        return suspendCoroutine { continuation ->
            database.child("orders")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot.childrenCount.toInt())
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(0)
                    }
                })
        }
    }

    private suspend fun getTotalRevenue(): Double {
        return suspendCoroutine { continuation ->
            database.child("orders")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var totalRevenue = 0.0
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(Order::class.java)
                            if (order?.orderStatus == OrderStatus.delivered) {
                                totalRevenue += order.total
                            }
                        }
                        continuation.resume(totalRevenue)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(0.0)
                    }
                })
        }
    }

    private suspend fun getLowStockProductsCount(): Int {
        return suspendCoroutine { continuation ->
            database.child("collections").child("shop")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var lowStockCount = 0
                        for (productSnapshot in snapshot.children) {
                            val product = productSnapshot.getValue(Product::class.java)
                            if (product?.isLowStock() == true) {
                                lowStockCount++
                            }
                        }
                        continuation.resume(lowStockCount)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(0)
                    }
                })
        }
    }

    private suspend fun getRecentOrders(limit: Int): List<Order> {
        return suspendCoroutine { continuation ->
            database.child("orders")
                .orderByChild("orderDate")
                .limitToLast(limit)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val orders = mutableListOf<Order>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(Order::class.java)
                            order?.let { orders.add(it) }
                        }
                        continuation.resume(orders.reversed()) // Most recent first
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(emptyList())
                    }
                })
        }
    }

    // Products Management
    fun getProducts(callback: (List<Product>) -> Unit) {
        database.child("collections").child("shop")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = mutableListOf<Product>()
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let { products.add(it) }
                    }
                    callback(products)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val productId = database.child("collections").child("shop").push().key ?: ""
            val productWithId = product.copy(id = productId)
            database.child("collections").child("shop").child(productId).setValue(productWithId).await()
            Result.success(productId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val updatedProduct = product.copy(updatedAt = System.currentTimeMillis())
            database.child("collections").child("shop").child(product.id).setValue(updatedProduct).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            database.child("collections").child("shop").child(productId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Orders Management
    fun getOrders(callback: (List<Order>) -> Unit) {
        database.child("orders")
            .orderByChild("orderDate")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Order>()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        order?.let { orders.add(it) }
                    }
                    callback(orders.reversed()) // Most recent first
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            database.child("orders").child(orderId).child("orderStatus").setValue(status.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteOrder(orderId: String): Result<Unit> {
        return try {
            database.child("orders").child(orderId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Users Management
    fun getUsers(callback: (List<User>) -> Unit) {
        database.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { users.add(it) }
                    }
                    callback(users)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    suspend fun addUser(user: User): Result<String> {
        return try {
            val userId = database.child("users").push().key ?: ""
            val userWithId = user.copy(id = userId)
            database.child("users").child(userId).setValue(userWithId).await()
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            database.child("users").child(user.id).setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            database.child("users").child(userId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Categories Management
    fun getCategories(callback: (List<Category>) -> Unit) {
        database.child("categories")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categories = mutableListOf<Category>()
                    for (categorySnapshot in snapshot.children) {
                        val category = categorySnapshot.getValue(Category::class.java)
                        category?.let { categories.add(it) }
                    }
                    callback(categories)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    suspend fun addCategory(category: Category): Result<String> {
        return try {
            val categoryId = database.child("categories").push().key ?: ""
            val categoryWithId = category.copy(id = categoryId)
            database.child("categories").child(categoryId).setValue(categoryWithId).await()
            Result.success(categoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(category: Category): Result<Unit> {
        return try {
            val updatedCategory = category.copy(updatedAt = System.currentTimeMillis())
            database.child("categories").child(category.id).setValue(updatedCategory).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            database.child("categories").child(categoryId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
