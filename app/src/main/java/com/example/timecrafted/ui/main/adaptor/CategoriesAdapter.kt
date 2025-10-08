package com.example.timecrafted.ui.main.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.databinding.ItemCategoriesBinding
import com.example.timecrafted.ui.main.data.Categories

class CategoriesAdapter(private val categories: List<Categories>) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.apply {
            categoriesName.text = category.name
            categoriesImage.setImageResource(category.imageResId)

            // ✅ Click listener for category
            root.setOnClickListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Category: ${category.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount() = categories.size
}
