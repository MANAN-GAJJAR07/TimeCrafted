package com.example.timecrafted.ui.main.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.text.NumberFormat
import java.util.Locale

@IgnoreExtraProperties
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val currency: String = "₹",
    val description: String = "",
    val category: String = "",
    val sections: List<String> = emptyList(),
    val imagePath: String = "",
    val imageUrl: String = "",
    val brand: String = "",
    val style: String = "",
    val bandMaterial: String = "",
    val caseSize: String = "",
    val createdAt: Long = 0L,
    val salesCount: Long = 0L,
    val rating: Double = 0.0
) : Serializable {
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
