package com.example.timecrafted.data.repository

import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.ui.product.model.CartItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    fun addToCart(
        userId: String,
        productId: String,
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child(FirebaseKeys.Cart.ROOT).child(userId).child(productId)
            .setValue(cartItem.copy(id = productId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to add to cart") }
    }

    fun updateCartItemQuantity(
        userId: String,
        productId: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child(FirebaseKeys.Cart.ROOT).child(userId).child(productId).child("quantity")
            .setValue(quantity)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update quantity") }
    }

    fun removeFromCart(
        userId: String,
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child(FirebaseKeys.Cart.ROOT).child(userId).child(productId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to remove from cart") }
    }

    fun clearCart(
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child(FirebaseKeys.Cart.ROOT).child(userId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to clear cart") }
    }
}

