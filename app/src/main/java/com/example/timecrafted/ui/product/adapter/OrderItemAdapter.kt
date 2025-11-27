package com.example.timecrafted.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.data.cloudinary.CloudinaryService
import com.example.timecrafted.databinding.ItemOrderItemBinding
import com.example.timecrafted.data.model.OrderItem
import java.text.NumberFormat
import java.util.Locale

class OrderItemAdapter : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    private val itemList = mutableListOf<OrderItem>()

    inner class OrderItemViewHolder(val binding: ItemOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.apply {
            tvItemName.text = item.name
            tvItemQuantity.text = "Quantity: ${item.quantity}"
            
            val format = NumberFormat.getNumberInstance(Locale.getDefault())
            format.minimumFractionDigits = 0
            format.maximumFractionDigits = 2
            val totalPrice = item.price * item.quantity
            tvItemPrice.text = "${item.currency}${format.format(totalPrice)}"

            val imageUrl = if (item.imagePath.isNotEmpty()) {
                CloudinaryService.getImageUrl(item.imagePath)
            } else {
                ""
            }

            Glide.with(imgItem)
                .load(imageUrl.ifBlank { null })
                .placeholder(R.drawable.product_img)
                .error(R.drawable.product_img)
                .into(imgItem)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(items: List<OrderItem>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }
}

