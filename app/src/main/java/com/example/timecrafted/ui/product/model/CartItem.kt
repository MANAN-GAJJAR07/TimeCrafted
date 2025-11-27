package com.example.timecrafted.ui.product.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.text.NumberFormat
import java.util.Locale

@IgnoreExtraProperties
data class CartItem(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val currency: String = "₹",
    val imagePath: String = "",
    val imageUrl: String = ""
) {
    @get:Exclude
    val formattedPrice: String
        get() = buildPriceLabel(price, currency)

    companion object {
        private fun buildPriceLabel(amount: Double, currencySymbol: String): String {
            val format = NumberFormat.getNumberInstance(Locale.getDefault())
            format.minimumFractionDigits = 0
            format.maximumFractionDigits = 2
            return "$currencySymbol${format.format(amount)}"
        }
    }
}


