package com.example.timecrafted.ui.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.model.Review
import com.example.timecrafted.data.repository.ReviewRepository
import com.example.timecrafted.ui.product.model.ReviewProductOption

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var productSpinner: Spinner
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewInput: EditText
    private lateinit var submitButton: Button

    private val reviewRepository by lazy { ReviewRepository() }
    private val authRepository by lazy { AuthRepository() }

    private var orderId: String = ""
    private var products: List<ReviewProductOption> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        orderId = intent.getStringExtra(EXTRA_ORDER_ID).orEmpty()
        @Suppress("UNCHECKED_CAST")
        products = intent.getSerializableExtra(EXTRA_PRODUCTS) as? ArrayList<ReviewProductOption>
            ?: emptyList()

        if (orderId.isEmpty() || products.isEmpty()) {
            finish()
            return
        }

        productSpinner = findViewById(R.id.productSpinner)
        ratingBar = findViewById(R.id.ratingBar)
        reviewInput = findViewById(R.id.reviewInput)
        submitButton = findViewById(R.id.submitReviewBtn)

        productSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            products.map { it.productName }
        )

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }

        submitButton.setOnClickListener {
            submitButton.isEnabled = false
            submitReview()
        }
    }

    private fun submitReview() {
        val rating = ratingBar.rating
        if (rating == 0f) {
            Toast.makeText(this, R.string.review_rating_required, Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
            return
        }

        val comment = reviewInput.text.toString().trim()
        if (comment.length < 5) {
            Toast.makeText(this, R.string.review_comment_required, Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
            return
        }

        val userId = authRepository.getCurrentUserId()
        if (userId.isNullOrBlank()) {
            Toast.makeText(this, R.string.review_login_required, Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
            return
        }

        val selectedProduct = products[productSpinner.selectedItemPosition]
        val review = Review(
            productId = selectedProduct.productId,
            productName = selectedProduct.productName,
            rating = rating.toDouble(),
            comment = comment,
            userId = userId,
            orderId = orderId
        )

        reviewRepository.submitReview(
            review = review,
            onSuccess = {
                runOnUiThread {
                    Toast.makeText(this, R.string.review_submit_success, Toast.LENGTH_LONG).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            },
            onError = {
                runOnUiThread {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                    submitButton.isEnabled = true
                }
            }
        )
    }

    companion object {
        private const val EXTRA_ORDER_ID = "extra_order_id"
        private const val EXTRA_PRODUCTS = "extra_products"

        fun createIntent(
            context: Context,
            orderId: String,
            products: ArrayList<ReviewProductOption>
        ): Intent {
            return Intent(context, WriteReviewActivity::class.java).apply {
                putExtra(EXTRA_ORDER_ID, orderId)
                putExtra(EXTRA_PRODUCTS, products)
            }
        }
    }
}

