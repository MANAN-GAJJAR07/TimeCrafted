package com.example.timecrafted.data.firebase

import com.example.timecrafted.data.cloudinary.CloudinaryService
import com.example.timecrafted.ui.main.data.Categories
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.model.CartItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class FirebaseListener(
    val reference: DatabaseReference,
    val listener: ValueEventListener
)

class FirebaseRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    fun listenForProducts(
        collectionKey: String,
        onResult: (List<Product>) -> Unit,
        onError: (Throwable) -> Unit
    ): FirebaseListener {
        val reference = database.child(FirebaseKeys.Collections.ROOT).child(collectionKey)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.mapProducts()
                attachStorageUrls(
                    items = products,
                    imagePathSelector = { it.imagePath },
                    copyWithUrl = { item, url -> item.copy(imageUrl = url) },
                    onResult = onResult,
                    onError = onError
                )
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        return FirebaseListener(reference, listener)
    }

    fun listenForCategories(
        onResult: (List<Categories>) -> Unit,
        onError: (Throwable) -> Unit
    ): FirebaseListener {
        val reference = database.child(FirebaseKeys.Categories.ROOT)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.mapCategories()
                attachStorageUrls(
                    items = categories,
                    imagePathSelector = { it.imagePath },
                    copyWithUrl = { item, url -> item.copy(imageUrl = url) },
                    onResult = onResult,
                    onError = onError
                )
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        return FirebaseListener(reference, listener)
    }

    fun listenForCartItems(
        userId: String,
        onResult: (List<CartItem>) -> Unit,
        onError: (Throwable) -> Unit
    ): FirebaseListener {
        val reference = database.child(FirebaseKeys.Cart.ROOT).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = snapshot.mapCartItems()
                attachStorageUrls(
                    items = cartItems,
                    imagePathSelector = { it.imagePath },
                    copyWithUrl = { item, url -> item.copy(imageUrl = url) },
                    onResult = onResult,
                    onError = onError
                )
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        return FirebaseListener(reference, listener)
    }

    fun removeListener(firebaseListener: FirebaseListener?) {
        firebaseListener?.let { listener ->
            listener.reference.removeEventListener(listener.listener)
        }
    }

    private fun DataSnapshot.mapProducts(): List<Product> =
        children.mapNotNull { child ->
            child.getValue(Product::class.java)?.copy(id = child.key.orEmpty())
        }

    private fun DataSnapshot.mapCategories(): List<Categories> =
        children.mapNotNull { child ->
            child.getValue(Categories::class.java)?.copy(id = child.key.orEmpty())
        }

    private fun DataSnapshot.mapCartItems(): List<CartItem> =
        children.mapNotNull { child ->
            child.getValue(CartItem::class.java)?.copy(id = child.key.orEmpty())
        }

    private fun <T : Any> attachStorageUrls(
        items: List<T>,
        imagePathSelector: (T) -> String,
        copyWithUrl: (T, String) -> T,
        onResult: (List<T>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (items.isEmpty()) {
            onResult(items)
            return
        }

        val updatedItems = items.toMutableList()
        var completedCount = 0

        fun emitIfComplete() {
            if (completedCount == items.size) {
                onResult(updatedItems)
            }
        }

        items.forEachIndexed { index, item ->
            val imagePath = imagePathSelector(item)
            if (imagePath.isBlank()) {
                completedCount++
                emitIfComplete()
                return@forEachIndexed
            }

            // Use Cloudinary to get image URL
            val imageUrl = CloudinaryService.getImageUrl(imagePath)
            updatedItems[index] = copyWithUrl(item, imageUrl)
            completedCount++
            emitIfComplete()
        }
    }

}


