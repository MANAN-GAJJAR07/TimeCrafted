package com.example.timecrafted.ui.product


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.ui.product.adapter.RelatedProductsAdapter
import com.example.timecrafted.databinding.ActivityProductDetailsBinding
import com.example.timecrafted.ui.product.model.RelatedProduct

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        val products = listOf(
            RelatedProduct("Classic Timepiece", "$900", R.drawable.product_img),
            RelatedProduct("Sport Chronograph", "$1,000", R.drawable.product_img),
            RelatedProduct("Elegant Watch", "$850", R.drawable.product_img)
        )

        val adapter = RelatedProductsAdapter(products) { product ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("PRODUCT_NAME", product.name)
            intent.putExtra("PRODUCT_PRICE", product.price)
            intent.putExtra("PRODUCT_IMAGE", product.imageRes)
            startActivity(intent)
        }

        binding.rvRelatedProducts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRelatedProducts.adapter = adapter
    }
}
