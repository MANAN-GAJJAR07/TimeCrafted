package com.example.timecrafted.ui.product

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R

class SortActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sort)

        val btnApply: Button = findViewById(R.id.btnApplySorting)
        val rgSort: RadioGroup = findViewById(R.id.rgSortOptions)

        btnApply.setOnClickListener {
            val selectedId = rgSort.checkedRadioButtonId
            val message = when (selectedId) {
                R.id.rbLowHigh -> "Price: Low to High"
                R.id.rbHighLow -> "Price: High to Low"
                R.id.rbNewest -> "Newest Arrivals"
                R.id.rbPopularity -> "Popularity"
                R.id.rbRating -> "Customer Rating"
                else -> "No option selected"
            }
            Toast.makeText(this, "Sorting by $message", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
