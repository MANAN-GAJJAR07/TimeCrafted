package com.example.timecrafted.data.repository

import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.data.model.Review
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReviewRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    fun submitReview(
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (review.productId.isBlank()) {
            onError("Invalid product")
            return
        }

        val reviewRef = database.child(FirebaseKeys.Collections.REVIEWS)
            .child(review.productId)
            .push()

        val reviewWithId = review.copy(id = reviewRef.key ?: "")
        reviewRef.setValue(reviewWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to submit review") }
    }
}

