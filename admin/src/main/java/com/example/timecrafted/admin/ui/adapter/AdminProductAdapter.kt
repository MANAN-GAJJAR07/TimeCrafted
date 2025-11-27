package com.example.timecrafted.admin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timecrafted.admin.R
import com.example.timecrafted.admin.data.model.Product
import com.example.timecrafted.admin.databinding.ItemAdminProductBinding

class AdminProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : ListAdapter<Product, AdminProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    fun updateProducts(products: List<Product>) {
        submitList(products)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemAdminProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                tvProductName.text = product.name
                tvProductPrice.text = product.getFormattedPrice()
                tvProductCategory.text = product.category.ifEmpty { "Uncategorized" }
                tvProductStock.text = "Stock: ${product.stock}"
                
                // Show/hide low stock warning
                if (product.isLowStock()) {
                    tvLowStockWarning.visibility = View.VISIBLE
                } else {
                    tvLowStockWarning.visibility = View.GONE
                }
                
                // Load product image
                if (product.imagePath.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(product.imagePath)
                        .placeholder(R.drawable.ic_products)
                        .error(R.drawable.ic_products)
                        .into(ivProductImage)
                } else {
                    ivProductImage.setImageResource(R.drawable.ic_products)
                }
                
                // Set click listeners
                btnEditProduct.setOnClickListener {
                    onEditClick(product)
                }
                
                btnDeleteProduct.setOnClickListener {
                    onDeleteClick(product)
                }
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
