package com.example.timecrafted.ui.product

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.databinding.ActivityCartBinding
import com.example.timecrafted.ui.product.adapter.CartItem
import com.example.timecrafted.ui.product.adapter.CartItemAdapter

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        val checkoutBtn = findViewById<Button>(R.id.checkoutBtn)

        checkoutBtn.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
            finish()
        }


        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cartItems = listOf(
            CartItem("Classic Chronograph Watch", 1, 250.0, R.drawable.product_img),
            CartItem("Sporty Digital Watch", 2, 180.0, R.drawable.product_img)
        )

        adapter = CartItemAdapter(cartItems)
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = adapter

        val subtotal = cartItems.sumOf { it.price }
        val taxes = 20.0
        val total = subtotal + taxes

        binding.tvSubtotal.text = "Subtotal: $${subtotal.toInt()}"
        binding.tvShipping.text = "Shipping: Free"
        binding.tvTaxes.text = "Taxes: $${taxes.toInt()}"
        binding.tvTotal.text = "Total: $${total.toInt()}"
    }
}
