package com.example.timecrafted.ui.main.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.databinding.ItemCategoriesBinding
import com.example.timecrafted.ui.main.data.Categories

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private val categories = mutableListOf<Categories>()

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
            Glide.with(categoriesImage)
                .load(category.imageUrl.ifBlank { null })
                .placeholder(R.drawable.product_img)
                .error(R.drawable.product_img)
                .into(categoriesImage)

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

    fun submitList(items: List<Categories>) {
        categories.clear()
        categories.addAll(items)
        notifyDataSetChanged()
    }
}
