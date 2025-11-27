package com.example.timecrafted.ui.product.model

import com.example.timecrafted.ui.main.data.Product
import java.io.Serializable
import java.util.Locale

data class ProductFilterOptions(
    val brands: Set<String> = emptySet(),
    val styles: Set<String> = emptySet(),
    val bandMaterials: Set<String> = emptySet(),
    val maxPrice: Double? = null
) : Serializable {

    fun isEmpty(): Boolean {
        return brands.isEmpty() && styles.isEmpty() && bandMaterials.isEmpty() && maxPrice == null
    }

    fun matches(product: Product): Boolean {
        val lowerBrand = product.brand.lowercase(Locale.getDefault())
        val lowerStyle = product.style.lowercase(Locale.getDefault())
        val lowerBand = product.bandMaterial.lowercase(Locale.getDefault())

        if (brands.isNotEmpty() && !brands.any { it.equals(lowerBrand, ignoreCase = true) }) {
            return false
        }

        if (styles.isNotEmpty() && !styles.any { it.equals(lowerStyle, ignoreCase = true) }) {
            return false
        }

        if (bandMaterials.isNotEmpty() && !bandMaterials.any { it.equals(lowerBand, ignoreCase = true) }) {
            return false
        }

        if (maxPrice != null && product.price > maxPrice) {
            return false
        }

        return true
    }
}

