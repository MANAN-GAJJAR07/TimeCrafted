package com.example.timecrafted.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.databinding.ActivityProductDetailBinding
import com.example.timecrafted.ui.product.adapter.RelatedProductsAdapter
import com.example.timecrafted.ui.product.model.RelatedProduct

class productDetail : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var relatedProductsAdapter: RelatedProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get product data from Intent
        val productName = intent.getStringExtra("name") ?: "Unknown Product"
        val productPrice = intent.getStringExtra("price") ?: "$0"
        val productDesc = intent.getStringExtra("desc") ?: "No description available."
        val productImage = intent.getIntExtra("imageRes", R.drawable.product_img)

        // Set data in UI
        binding.tvTitle.text = productName
        binding.tvPrice.text = productPrice
        binding.tvShortDesc.text = productDesc
        binding.productImage.setImageResource(productImage)

        // Toolbar back button
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Add to Cart button
        binding.btnAddToCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // Add to Wishlist button → open Wishlist tab in MainActivity
        binding.btnWishlist.setOnClickListener {
            val intent = Intent(this, com.example.timecrafted.ui.main.MainActivity::class.java)
            intent.putExtra("openFragment", "wishlist")
            startActivity(intent)
            finish()
        }

        // Setup Related Products RecyclerView
        setupRelatedProducts()
    }

    private fun setupRelatedProducts() {
        // Sample related products list
        val relatedList = listOf(
            RelatedProduct(R.drawable.product_img, "Chrono Master", "$1,100", "Elegant chronograph watch"),
            RelatedProduct(R.drawable.product_img2, "Classic Automatic", "$980", "Stylish automatic watch"),
            RelatedProduct(R.drawable.product_img3, "Luxury Sport", "$1,450", "Sporty luxury watch"),
            RelatedProduct(R.drawable.product_img4, "Elegant Minimalist", "$890", "Minimalist design watch")
        )

        relatedProductsAdapter = RelatedProductsAdapter(relatedList) { product ->
            // On click → open productDetail with this product data
            val intent = Intent(this, productDetail::class.java)
            intent.putExtra("name", product.name)
            intent.putExtra("price", product.price)
            intent.putExtra("desc", product.description)
            intent.putExtra("imageRes", product.imageRes)
            startActivity(intent)
        }

        binding.rvRelatedProducts.apply {
            layoutManager = LinearLayoutManager(this@productDetail, LinearLayoutManager.HORIZONTAL, false)
            adapter = relatedProductsAdapter
        }
    }
}
