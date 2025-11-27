package com.example.timecrafted.ui.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.ui.product.model.ProductSortOption

class SortActivity : AppCompatActivity() {

    private lateinit var sortRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sort)

        sortRadioGroup = findViewById(R.id.rgSortOptions)
        val applyButton: Button = findViewById(R.id.btnApplySorting)
        val backButton: ImageView = findViewById(R.id.backBtn)

        val initialSort =
            intent.getSerializableExtra(EXTRA_INITIAL_SORT) as? ProductSortOption ?: ProductSortOption.NONE
        preselectRadio(initialSort)

        applyButton.setOnClickListener {
            val selectedSort = mapSelectionToSortOption()
            val data = Intent().apply {
                putExtra(EXTRA_SORT_RESULT, selectedSort)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        backButton.setOnClickListener { finish() }
    }

    private fun preselectRadio(sortOption: ProductSortOption) {
        val radioId = when (sortOption) {
            ProductSortOption.PRICE_LOW_HIGH -> R.id.rbLowHigh
            ProductSortOption.PRICE_HIGH_LOW -> R.id.rbHighLow
            ProductSortOption.NEWEST -> R.id.rbNewest
            ProductSortOption.POPULARITY -> R.id.rbPopularity
            ProductSortOption.RATING -> R.id.rbRating
            ProductSortOption.NONE -> null
        }
        radioId?.let(sortRadioGroup::check)
    }

    private fun mapSelectionToSortOption(): ProductSortOption {
        return when (sortRadioGroup.checkedRadioButtonId) {
            R.id.rbLowHigh -> ProductSortOption.PRICE_LOW_HIGH
            R.id.rbHighLow -> ProductSortOption.PRICE_HIGH_LOW
            R.id.rbNewest -> ProductSortOption.NEWEST
            R.id.rbPopularity -> ProductSortOption.POPULARITY
            R.id.rbRating -> ProductSortOption.RATING
            else -> ProductSortOption.NONE
        }
    }

    companion object {
        const val EXTRA_SORT_RESULT = "extra_sort_result"
        const val EXTRA_INITIAL_SORT = "extra_initial_sort"

        fun createIntent(context: Context, currentSort: ProductSortOption?): Intent {
            return Intent(context, SortActivity::class.java).apply {
                currentSort?.let { putExtra(EXTRA_INITIAL_SORT, it) }
            }
        }
    }
}
