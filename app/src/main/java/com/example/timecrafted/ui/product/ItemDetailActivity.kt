package com.example.timecrafted.ui.product

import com.example.timecrafted.databinding.ActivityItemDetailBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("PRODUCT_NAME")
        val price = intent.getStringExtra("PRODUCT_PRICE")
        val image = intent.getIntExtra("PRODUCT_IMAGE", R.drawable.product_img)

        binding.tvProductNameDetail.text = name
        binding.tvProductPriceDetail.text = price
        binding.imgProductDetail.setImageResource(image)
    }
}
