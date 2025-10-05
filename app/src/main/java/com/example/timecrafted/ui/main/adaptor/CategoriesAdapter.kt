package com.example.timecrafted.ui.main.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
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
        val product = categories[position]
        holder.binding.apply {
            categoriesName.text = product.name
            categoriesImage.setImageResource(product.imageResId)
        }
    }

    override fun getItemCount() = categories.size

}