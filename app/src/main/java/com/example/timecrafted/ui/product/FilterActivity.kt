package com.example.timecrafted.ui.product

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.ui.main.fragment.ShopFragment

class FilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        val filter = findViewById<Button>(R.id.applyFiltersBtn)

        filter.setOnClickListener {
            Toast.makeText(this, "Filters Applied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
