package com.example.timecrafted.ui.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.ui.product.model.ProductFilterOptions
import java.text.NumberFormat
import java.util.Locale

class FilterActivity : AppCompatActivity() {

    private lateinit var applyFiltersBtn: Button
    private lateinit var priceSeekBar: SeekBar
    private lateinit var priceValueText: TextView
    private var currentFilter: ProductFilterOptions? = null

    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        currentFilter = intent.getSerializableExtra(EXTRA_INITIAL_FILTER) as? ProductFilterOptions

        applyFiltersBtn = findViewById(R.id.applyFiltersBtn)
        priceSeekBar = findViewById(R.id.priceSeekBar)
        priceValueText = findViewById(R.id.tvPriceValue)

        numberFormat.minimumFractionDigits = 0

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }

        setupSeekBar()
        prefillSelections()

        applyFiltersBtn.setOnClickListener {
            val filter = buildFilterOptions()
            val data = Intent().apply {
                putExtra(EXTRA_FILTER_RESULT, filter)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private fun setupSeekBar() {
        priceSeekBar.max = PRICE_MAX_PROGRESS
        priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePriceLabel(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        val initialProgress = currentFilter?.maxPrice?.let { price ->
            (price / PRICE_STEP).toInt().coerceIn(0, PRICE_MAX_PROGRESS)
        } ?: PRICE_MAX_PROGRESS
        priceSeekBar.progress = initialProgress
        updatePriceLabel(initialProgress)
    }

    private fun updatePriceLabel(progress: Int) {
        priceValueText.text = if (progress >= PRICE_MAX_PROGRESS) {
            getString(R.string.filter_price_any)
        } else {
            val price = progress * PRICE_STEP
            getString(R.string.filter_price_value, numberFormat.format(price))
        }
    }

    private fun buildFilterOptions(): ProductFilterOptions {
        val selectedBrands = BRAND_CHECKBOX_IDS
            .mapNotNull { findViewById<CheckBox>(it) }
            .filter { it.isChecked }
            .map { it.text.toString().lowercase(Locale.getDefault()) }
            .toSet()

        val selectedStyles = STYLE_CHECKBOX_IDS
            .mapNotNull { findViewById<CheckBox>(it) }
            .filter { it.isChecked }
            .map { it.text.toString().lowercase(Locale.getDefault()) }
            .toSet()

        val selectedBandMaterials = BAND_CHECKBOX_IDS
            .mapNotNull { findViewById<CheckBox>(it) }
            .filter { it.isChecked }
            .map { it.text.toString().lowercase(Locale.getDefault()) }
            .toSet()

        val maxPrice = priceSeekBar.progress.takeIf { it < PRICE_MAX_PROGRESS }?.let {
            it * PRICE_STEP.toDouble()
        }

        return ProductFilterOptions(
            brands = selectedBrands,
            styles = selectedStyles,
            bandMaterials = selectedBandMaterials,
            maxPrice = maxPrice
        )
    }

    private fun prefillSelections() {
        val filter = currentFilter ?: return

        BRAND_CHECKBOX_IDS
            .mapNotNull { findViewById<CheckBox>(it) }
            .forEach { checkBox ->
                val label = checkBox.text.toString().lowercase(Locale.getDefault())
                checkBox.isChecked = filter.brands.contains(label)
            }

        STYLE_CHECKBOX_IDS
            .mapNotNull { findViewById<CheckBox>(it) }
            .forEach { checkBox ->
                val label = checkBox.text.toString().lowercase(Locale.getDefault())
                checkBox.isChecked = filter.styles.contains(label)
            }

        BAND_CHECKBOX_IDS
            .mapNotNull { findViewById<CheckBox>(it) }
            .forEach { checkBox ->
                val label = checkBox.text.toString().lowercase(Locale.getDefault())
                checkBox.isChecked = filter.bandMaterials.contains(label)
            }
    }

    companion object {
        const val EXTRA_FILTER_RESULT = "extra_filter_result"
        const val EXTRA_INITIAL_FILTER = "extra_initial_filter"
        private const val PRICE_STEP = 5_000
        private const val PRICE_MAX_PROGRESS = 120

        private val BRAND_CHECKBOX_IDS = listOf(
            R.id.cbBrandRolex,
            R.id.cbBrandOmega,
            R.id.cbBrandTagHeuer,
            R.id.cbBrandSeiko,
            R.id.cbBrandCitizen
        )

        private val STYLE_CHECKBOX_IDS = listOf(
            R.id.cbStyleDress,
            R.id.cbStyleSport,
            R.id.cbStyleCasual
        )

        private val BAND_CHECKBOX_IDS = listOf(
            R.id.cbBandLeather,
            R.id.cbBandMetal,
            R.id.cbBandRubber,
            R.id.cbBandFabric
        )

        fun createIntent(context: Context, currentFilter: ProductFilterOptions?): Intent {
            return Intent(context, FilterActivity::class.java).apply {
                currentFilter?.let {
                    putExtra(EXTRA_INITIAL_FILTER, it)
                }
            }
        }
    }
}
