package com.example.timecrafted.ui.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.ui.main.data.Product

class RelatedProductsAdapter(
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<RelatedProductsAdapter.ViewHolder>() {

    private val productList = mutableListOf<Product>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgRelated)
        val tvName: TextView = itemView.findViewById(R.id.tvRelatedName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvRelatedPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_related_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        Glide.with(holder.imgProduct)
            .load(product.imageUrl.ifBlank { null })
            .placeholder(R.drawable.product_img)
            .error(R.drawable.product_img)
            .into(holder.imgProduct)

        holder.tvName.text = product.name
        holder.tvPrice.text = product.formattedPrice

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun submitList(items: List<Product>) {
        productList.clear()
        productList.addAll(items)
        notifyDataSetChanged()
    }
}
