package com.example.timecrafted.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.data.cloudinary.CloudinaryService
import com.example.timecrafted.databinding.ItemOrderHistoryBinding
import com.example.timecrafted.data.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryAdapter(
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    private val orderList = mutableListOf<Order>()

    inner class OrderViewHolder(val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.binding.apply {
            tvOrderNumber.text = "Order #${order.id.take(8)}"

            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = Date(order.createdAt)
            val dateText = dateFormat.format(date)

            tvOrderDate.text = "Date: $dateText | Status: ${order.orderStatus.replaceFirstChar { it.uppercaseChar() }}"

            // Format total
            val format = java.text.NumberFormat.getNumberInstance(Locale.getDefault())
            format.minimumFractionDigits = 0
            format.maximumFractionDigits = 2
            tvOrderTotal.text = "Total: ${order.currency}${format.format(order.total)}"

            // Load first item image if available
            order.items.firstOrNull()?.let { item ->
                val imageUrl = if (item.imagePath.isNotEmpty()) {
                    CloudinaryService.getImageUrl(item.imagePath)
                } else {
                    ""
                }

                Glide.with(imgOrder)
                    .load(imageUrl.ifBlank { null })
                    .placeholder(R.drawable.product_img)
                    .error(R.drawable.product_img)
                    .into(imgOrder)
            } ?: run {
                imgOrder.setImageResource(R.drawable.product_img)
            }

            // Click listener
            root.setOnClickListener {
                onOrderClick(order)
            }
        }
    }

    override fun getItemCount(): Int = orderList.size

    fun submitList(orders: List<Order>) {
        orderList.clear()
        orderList.addAll(orders)
        notifyDataSetChanged()
    }
}

