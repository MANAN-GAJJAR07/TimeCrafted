package com.example.timecrafted.ui.product.model

import java.io.Serializable

data class ReviewProductOption(
    val productId: String,
    val productName: String
) : Serializable {
    override fun toString(): String = productName
}

