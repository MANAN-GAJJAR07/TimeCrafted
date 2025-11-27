package com.example.timecrafted.data.repository

import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.data.model.Order
import com.example.timecrafted.data.model.OrderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class OrderRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    fun createOrder(
        order: Order,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val orderRef = database.child("orders").push()
        val orderId = orderRef.key ?: ""
        val orderWithId = order.copy(id = orderId, updatedAt = System.currentTimeMillis())
        
        orderRef.setValue(orderWithId)
            .addOnSuccessListener {
                updateProductSalesCounts(order.items)
                onSuccess(orderId)
            }
            .addOnFailureListener { onError(it.message ?: "Failed to create order") }
    }

    fun getUserOrders(
        userId: String,
        onResult: (List<Order>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {
        val reference = database.child("orders").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { child ->
                    child.getValue(Order::class.java)?.copy(id = child.key ?: "")
                }.sortedByDescending { it.createdAt }
                onResult(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }
        reference.addValueEventListener(listener)
        return listener
    }

    fun updateOrderStatus(
        orderId: String,
        status: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("orders").child(orderId).child("orderStatus").setValue(status)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update order") }
    }

    fun removeOrderListener(listener: ValueEventListener) {
        database.child("orders").removeEventListener(listener)
    }

    fun getOrderById(
        orderId: String,
        onSuccess: (Order) -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("orders").child(orderId).get()
            .addOnSuccessListener { snapshot ->
                val order = snapshot.getValue(Order::class.java)?.copy(id = snapshot.key ?: "")
                if (order != null) {
                    onSuccess(order)
                } else {
                    onError("Order not found")
                }
            }
            .addOnFailureListener { onError(it.message ?: "Failed to get order") }
    }

    private fun updateProductSalesCounts(items: List<OrderItem>) {
        items.forEach { item ->
            if (item.productId.isBlank() || item.quantity <= 0) return@forEach

            val salesRef = database.child(FirebaseKeys.Collections.ROOT)
                .child(FirebaseKeys.Collections.SHOP)
                .child(item.productId)
                .child("salesCount")

            salesRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentValue = currentData.getValue(Long::class.java) ?: 0L
                    currentData.value = currentValue + item.quantity
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    error?.toException()?.printStackTrace()
                }
            })
        }
    }
}

