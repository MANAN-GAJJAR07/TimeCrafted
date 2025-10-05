package com.example.timecrafted.ui.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.R
import com.example.timecrafted.ui.product.model.RelatedProduct

class RelatedProductsAdapter(
    private val productList: List<RelatedProduct>,
    private val onItemClick: (RelatedProduct) -> Unit
) : RecyclerView.Adapter<RelatedProductsAdapter.ViewHolder>() {

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
        holder.imgProduct.setImageResource(product.imageRes)
        holder.tvName.text = product.name
        holder.tvPrice.text = product.price

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount(): Int = productList.size
}
