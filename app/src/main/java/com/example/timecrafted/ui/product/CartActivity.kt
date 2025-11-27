package com.example.timecrafted.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.data.firebase.FirebaseListener
import com.example.timecrafted.data.firebase.FirebaseRepository
import com.example.timecrafted.data.repository.CartRepository
import com.example.timecrafted.databinding.ActivityCartBinding
import com.example.timecrafted.ui.product.adapter.CartItemAdapter
import com.example.timecrafted.ui.product.model.CartItem
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartItemAdapter

    private val repository by lazy { FirebaseRepository() }
    private val cartRepository by lazy { CartRepository() }
    private val authRepository by lazy { AuthRepository() }
    private var cartListener: FirebaseListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check authentication
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            finish()
            return
        }

        setupRecyclerView(userId)
        observeCartItems(userId)
        binding.checkoutBtn.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun setupRecyclerView(userId: String) {
        adapter = CartItemAdapter(
            onQuantityChanged = { productId, quantity ->
                cartRepository.updateCartItemQuantity(
                    userId = userId,
                    productId = productId,
                    quantity = quantity,
                    onSuccess = {},
                    onError = { error ->
                        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                    }
                )
            },
            onDelete = { productId ->
                cartRepository.removeFromCart(
                    userId = userId,
                    productId = productId,
                    onSuccess = {
                        Snackbar.make(binding.root, "Item removed from cart", Snackbar.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = adapter
    }

    private fun observeCartItems(userId: String) {
        toggleLoading(true)
        cartListener = repository.listenForCartItems(
            userId, // Use userId instead of DEFAULT_BUCKET
            onResult = { items ->
                toggleLoading(false)
                adapter.submitList(items)
                binding.emptyCartState.isVisible = items.isEmpty()
                updateSummary(items)
            },
            onError = { error ->
                toggleLoading(false)
                handleError(error)
            }
        )
    }

    private fun updateSummary(cartItems: List<CartItem>) {
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val currencySymbol = cartItems.firstOrNull()?.currency ?: "₹"
        val shipping = if (cartItems.isEmpty() || subtotal >= 5000) 0.0 else 149.0
        val taxes = subtotal * 0.05
        val total = subtotal + shipping + taxes

        binding.tvSubtotal.text =
            getString(R.string.cart_subtotal_label, formatCurrency(subtotal, currencySymbol))
        binding.tvShipping.text =
            getString(
                R.string.cart_shipping_label,
                if (shipping == 0.0) "Free" else formatCurrency(shipping, currencySymbol)
            )
        binding.tvTaxes.text =
            getString(R.string.cart_taxes_label, formatCurrency(taxes, currencySymbol))
        binding.tvTotal.text =
            getString(R.string.cart_total_label, formatCurrency(total, currencySymbol))
    }

    private fun formatCurrency(amount: Double, currencySymbol: String): String {
        val format = NumberFormat.getNumberInstance(Locale.getDefault())
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 2
        return "$currencySymbol${format.format(amount)}"
    }

    private fun toggleLoading(show: Boolean) {
        binding.cartProgress.isVisible = show
        if (show) {
            binding.emptyCartState.isVisible = false
        }
    }

    private fun handleError(error: Throwable) {
        Snackbar.make(
            binding.root,
            error.message ?: getString(R.string.generic_error_message),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.removeListener(cartListener)
    }
}
