package com.example.timecrafted.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.databinding.ItemProductBinding
import com.example.timecrafted.ui.main.data.Product

class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val products = mutableListOf<Product>()

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            productName.text = product.name
            productPrice.text = product.formattedPrice

            Glide.with(productImage)
                .load(product.imageUrl.ifBlank { null })
                .placeholder(R.drawable.product_img)
                .error(R.drawable.product_img)
                .into(productImage)

            root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun getItemCount() = products.size

    fun submitList(items: List<Product>) {
        products.clear()
        products.addAll(items)
        notifyDataSetChanged()
    }
}
