package com.example.timecrafted.data.repository

import com.example.timecrafted.data.cloudinary.CloudinaryService
import com.example.timecrafted.data.firebase.FirebaseListener
import com.example.timecrafted.ui.main.data.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WishlistRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    fun addToWishlist(
        userId: String,
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("wishlist").child(userId).child(product.id)
            .setValue(product)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to add to wishlist") }
    }

    fun removeFromWishlist(
        userId: String,
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("wishlist").child(userId).child(productId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to remove from wishlist") }
    }

    fun isInWishlist(
        userId: String,
        productId: String,
        onResult: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("wishlist").child(userId).child(productId).get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.exists())
            }
            .addOnFailureListener { onError(it.message ?: "Failed to check wishlist") }
    }

    fun listenForWishlist(
        userId: String,
        onResult: (List<Product>) -> Unit,
        onError: (Throwable) -> Unit
    ): FirebaseListener {
        val reference = database.child("wishlist").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.children.mapNotNull { child ->
                    child.getValue(Product::class.java)?.copy(id = child.key.orEmpty())
                }
                
                // Attach image URLs
                if (products.isEmpty()) {
                    onResult(products)
                    return
                }

                val updatedProducts = products.map { product ->
                    val imageUrl = if (product.imagePath.isNotEmpty()) {
                        CloudinaryService.getImageUrl(product.imagePath)
                    } else {
                        product.imageUrl
                    }
                    product.copy(imageUrl = imageUrl)
                }
                
                onResult(updatedProducts)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        return FirebaseListener(reference, listener)
    }
}

