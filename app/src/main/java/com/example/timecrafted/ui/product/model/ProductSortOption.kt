package com.example.timecrafted.ui.product.model

import java.io.Serializable

enum class ProductSortOption : Serializable {
    NONE,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    NEWEST,
    POPULARITY,
    RATING
}

