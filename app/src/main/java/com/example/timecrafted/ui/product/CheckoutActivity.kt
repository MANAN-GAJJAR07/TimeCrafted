package com.example.timecrafted.ui.product

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.data.firebase.FirebaseListener
import com.example.timecrafted.data.firebase.FirebaseRepository
import com.example.timecrafted.data.model.Address
import com.example.timecrafted.data.model.Order
import com.example.timecrafted.data.model.OrderItem
import com.example.timecrafted.data.repository.CartRepository
import com.example.timecrafted.data.repository.OrderRepository
import com.example.timecrafted.data.repository.UserRepository
import com.example.timecrafted.ui.product.model.CartItem
import com.google.android.material.snackbar.Snackbar
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var radioCod: RadioButton
    private lateinit var radioRazorpay: RadioButton
    private lateinit var tvTaxes: TextView
    private lateinit var tvPlatformFee: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnPlaceOrder: Button
    private lateinit var tvAddressName: TextView
    private lateinit var tvAddressPhone: TextView
    private lateinit var tvAddressFull: TextView
    private val authRepository by lazy { AuthRepository() }
    private val cartRepository by lazy { CartRepository() }
    private val orderRepository by lazy { OrderRepository() }
    private val userRepository by lazy { UserRepository() }
    private val firebaseRepository by lazy { FirebaseRepository() }
    
    private var cartItems: List<CartItem> = emptyList()
    private var selectedAddress: Address? = null
    private var cartListener: FirebaseListener? = null
    private var isCodSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        
        radioCod = findViewById(R.id.radioCod)
        radioRazorpay = findViewById(R.id.radioRazorpay)
        tvTaxes = findViewById(R.id.tvTaxes)
        tvPlatformFee = findViewById(R.id.tvPlatformFee)
        tvTotal = findViewById(R.id.tvTotal)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        tvAddressName = findViewById(R.id.tvAddressName)
        tvAddressPhone = findViewById(R.id.tvAddressPhone)
        tvAddressFull = findViewById(R.id.tvAddressFull)

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            finish()
            return
        }

        Checkout.preload(applicationContext)

        setupPaymentMethodSelection()
        loadCartItems(userId)
        loadDefaultAddress(userId)
        setupPlaceOrderButton()
    }

    private fun setupPaymentMethodSelection() {
        radioCod.isChecked = true
        radioCod.setOnClickListener {
            isCodSelected = true
            updateOrderSummary()
        }
        radioRazorpay.setOnClickListener {
            isCodSelected = false
            updateOrderSummary()
        }
    }

    private fun loadCartItems(userId: String) {
        cartListener = firebaseRepository.listenForCartItems(
            userId,
            onResult = { items ->
                cartItems = items
                updateOrderSummary()
            },
            onError = { error ->
                Snackbar.make(findViewById(android.R.id.content), error.message ?: "Failed to load cart", Snackbar.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadDefaultAddress(userId: String) {
        val listener = userRepository.getUserAddresses(
            userId,
            onResult = { addresses ->
                selectedAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
                updateAddressDisplay()
            },
            onError = { }
        )
    }

    private fun updateAddressDisplay() {
        selectedAddress?.let { address ->
            tvAddressName.text = address.fullName
            tvAddressPhone.text = "Phone: ${address.phoneNumber}"
            val addressText = buildString {
                append(address.addressLine1)
                if (address.addressLine2.isNotEmpty()) {
                    append(", ${address.addressLine2}")
                }
                append(", ${address.city}, ${address.state} ${address.zipCode}")
                if (address.country.isNotEmpty()) {
                    append(", ${address.country}")
                }
            }
            tvAddressFull.text = addressText
        } ?: run {
            tvAddressName.text = "No address selected"
            tvAddressPhone.text = ""
            tvAddressFull.text = "Please add an address"
        }
    }

    private fun updateOrderSummary() {
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val currency = cartItems.firstOrNull()?.currency ?: "₹"
        val shipping = if (cartItems.isEmpty() || subtotal >= 5000) 0.0 else 149.0
        val taxes = subtotal * 0.05
        val platformFee = if (isCodSelected) 10.0 else 0.0
        val total = subtotal + shipping + taxes + platformFee

        tvTaxes.text = "Taxes       ${formatCurrency(taxes, currency)}"
        
        if (isCodSelected) {
            tvPlatformFee.visibility = android.view.View.VISIBLE
            tvPlatformFee.text = "Platform Fee   ${formatCurrency(platformFee, currency)}"
        } else {
            tvPlatformFee.visibility = android.view.View.GONE
        }

        tvTotal.text = "Total   ${formatCurrency(total, currency)}"
    }

    private fun formatCurrency(amount: Double, currencySymbol: String): String {
        val format = NumberFormat.getNumberInstance(Locale.getDefault())
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 2
        return "$currencySymbol${format.format(amount)}"
    }

    private fun setupPlaceOrderButton() {
        btnPlaceOrder.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedAddress == null) {
                Toast.makeText(this, "Please select a shipping address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isCodSelected) {
                createOrder("cod")
            } else {
                initiateRazorpayPayment()
            }
        }
    }

    private fun createOrder(paymentMethod: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val currency = cartItems.firstOrNull()?.currency ?: "₹"
        val shipping = if (subtotal >= 5000) 0.0 else 149.0
        val taxes = subtotal * 0.05
        val platformFee = if (paymentMethod == "cod") 10.0 else 0.0
        val total = subtotal + shipping + taxes + platformFee

        val orderItems = cartItems.map { item ->
            OrderItem(
                productId = item.id,
                name = item.name,
                price = item.price,
                quantity = item.quantity,
                imagePath = item.imagePath,
                currency = item.currency
            )
        }

        val order = Order(
            userId = userId,
            items = orderItems,
            subtotal = subtotal,
            shipping = shipping,
            taxes = taxes,
            platformFee = platformFee,
            total = total,
            currency = currency,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "cod") "pending" else "paid",
            orderStatus = "pending",
            shippingAddress = selectedAddress
        )

        orderRepository.createOrder(
            order = order,
            onSuccess = { orderId ->
                // Clear cart
                cartRepository.clearCart(
                    userId = userId,
                    onSuccess = {
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, OrderConfirmationActivity::class.java)
                        intent.putExtra("orderId", orderId)
                        startActivity(intent)
                        finish()
                    },
                    onError = { }
                )
            },
            onError = { error ->
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show()
            }
        )
    }

    private fun initiateRazorpayPayment() {
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val shipping = if (subtotal >= 5000) 0.0 else 149.0
        val taxes = subtotal * 0.05
        val total = (subtotal + shipping + taxes) * 100 // Convert to paise

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_0pyMCYHtidOX7B") // Replace with your Razorpay key

        try {
            val options = JSONObject()
            options.put("name", "TimeCrafted")
            options.put("description", "Order Payment")
            options.put("currency", "INR")
            options.put("amount", total.toInt())
            options.put("prefill.email", authRepository.getCurrentUserEmail() ?: "")
            
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        createOrder("razorpay")
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRepository.removeListener(cartListener)
    }
}
