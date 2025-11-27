package com.example.timecrafted.ui.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.data.firebase.FirebaseListener
import com.example.timecrafted.data.firebase.FirebaseRepository
import com.example.timecrafted.data.repository.CartRepository
import com.example.timecrafted.data.repository.WishlistRepository
import com.example.timecrafted.databinding.ActivityProductDetailBinding
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.adapter.RelatedProductsAdapter
import com.example.timecrafted.ui.product.model.CartItem
import com.google.android.material.snackbar.Snackbar

class productDetail : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var relatedProductsAdapter: RelatedProductsAdapter

    private val repository by lazy { FirebaseRepository() }
    private val cartRepository by lazy { CartRepository() }
    private val wishlistRepository by lazy { WishlistRepository() }
    private val authRepository by lazy { AuthRepository() }
    private var relatedListener: FirebaseListener? = null
    private var currentProduct: Product? = null
    private var isInWishlist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = extractProductFromIntent()
        currentProduct = product
        renderProduct(product)
        setupRelatedProducts()
        checkWishlistStatus(product.id)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnAddToCart.setOnClickListener {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                Snackbar.make(binding.root, "Please login to add items to cart", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cartItem = CartItem(
                id = product.id,
                name = product.name,
                quantity = 1,
                price = product.price,
                currency = product.currency,
                imagePath = product.imagePath,
                imageUrl = product.imageUrl
            )

            cartRepository.addToCart(
                userId = userId,
                productId = product.id,
                cartItem = cartItem,
                onSuccess = {
                    Snackbar.make(binding.root, "Added to cart", Snackbar.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnWishlist.setOnClickListener {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                Snackbar.make(binding.root, "Please login to add items to wishlist", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isInWishlist) {
                wishlistRepository.removeFromWishlist(
                    userId = userId,
                    productId = product.id,
                    onSuccess = {
                        isInWishlist = false
                        updateWishlistButton()
                        Snackbar.make(binding.root, "Removed from wishlist", Snackbar.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                    }
                )
            } else {
                wishlistRepository.addToWishlist(
                    userId = userId,
                    product = product,
                    onSuccess = {
                        isInWishlist = true
                        updateWishlistButton()
                        Snackbar.make(binding.root, "Added to wishlist", Snackbar.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun checkWishlistStatus(productId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        wishlistRepository.isInWishlist(
            userId = userId,
            productId = productId,
            onResult = { inWishlist ->
                isInWishlist = inWishlist
                updateWishlistButton()
            },
            onError = { }
        )
    }

    private fun updateWishlistButton() {
        // Update button appearance based on wishlist status
        // You can change icon or text here
    }

    private fun renderProduct(product: Product) {
        binding.tvTitle.text = product.name.ifBlank { getString(R.string.app_name) }
        binding.tvPrice.text = product.formattedPrice
        binding.tvShortDesc.text = product.description.ifBlank {
            getString(R.string.product_description_fallback)
        }

        Glide.with(this)
            .load(product.imageUrl.ifBlank { null })
            .placeholder(R.drawable.product_img)
            .error(R.drawable.product_img)
            .into(binding.productImage)
    }

    private fun setupRelatedProducts() {
        relatedProductsAdapter = RelatedProductsAdapter { product ->
            startActivity(createIntent(this, product))
        }

        binding.rvRelatedProducts.apply {
            layoutManager = LinearLayoutManager(
                this@productDetail,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = relatedProductsAdapter
        }

        binding.relatedProgress.isVisible = true

        relatedListener = repository.listenForProducts(
            FirebaseKeys.Collections.RELATED,
            onResult = { items ->
                binding.relatedProgress.isVisible = false
                relatedProductsAdapter.submitList(items.filter { it.id.isNotBlank() })
            },
            onError = { error ->
                binding.relatedProgress.isVisible = false
                handleError(error)
            }
        )
    }

    private fun extractProductFromIntent(): Product {
        val id = intent.getStringExtra(EXTRA_PRODUCT_ID).orEmpty()
        val name = intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty()
        val price = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        val description = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION).orEmpty()
        val imageUrl = intent.getStringExtra(EXTRA_PRODUCT_IMAGE_URL).orEmpty()
        val imagePath = intent.getStringExtra(EXTRA_PRODUCT_IMAGE_PATH).orEmpty()
        val currency = intent.getStringExtra(EXTRA_PRODUCT_CURRENCY) ?: "₹"

        return Product(
            id = id,
            name = name,
            price = price,
            description = description,
            currency = currency,
            imageUrl = imageUrl,
            imagePath = imagePath
        )
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
        repository.removeListener(relatedListener)
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "extra_product_id"
        private const val EXTRA_PRODUCT_NAME = "extra_product_name"
        private const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        private const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        private const val EXTRA_PRODUCT_IMAGE_URL = "extra_product_image_url"
        private const val EXTRA_PRODUCT_IMAGE_PATH = "extra_product_image_path"
        private const val EXTRA_PRODUCT_CURRENCY = "extra_product_currency"

        fun createIntent(context: Context, product: Product): Intent {
            return Intent(context, productDetail::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, product.id)
                putExtra(EXTRA_PRODUCT_NAME, product.name)
                putExtra(EXTRA_PRODUCT_PRICE, product.price)
                putExtra(EXTRA_PRODUCT_DESCRIPTION, product.description)
                putExtra(EXTRA_PRODUCT_IMAGE_URL, product.imageUrl)
                putExtra(EXTRA_PRODUCT_IMAGE_PATH, product.imagePath)
                putExtra(EXTRA_PRODUCT_CURRENCY, product.currency)
            }
        }
    }
}


