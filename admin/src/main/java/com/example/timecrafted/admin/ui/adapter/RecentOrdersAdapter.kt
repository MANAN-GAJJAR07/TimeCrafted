package com.example.timecrafted.admin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.admin.R
import com.example.timecrafted.admin.data.model.Order
import com.example.timecrafted.admin.data.model.OrderStatus
import com.example.timecrafted.admin.databinding.ItemRecentOrderBinding

class RecentOrdersAdapter(
    private val onOrderClick: (Order) -> Unit
) : ListAdapter<Order, RecentOrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    fun updateOrders(orders: List<Order>) {
        submitList(orders)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemRecentOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemRecentOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = "Order #${order.id.take(8)}"
                tvCustomerName.text = order.customerName.ifEmpty { "Unknown Customer" }
                tvOrderTotal.text = order.getFormattedTotal()
                tvOrderDate.text = order.getFormattedDate()
                tvOrderStatus.text = order.orderStatus.displayName
                
                // Set status chip color
                val statusColor = when (order.orderStatus) {
                    OrderStatus.pending -> R.color.status_pending
                    OrderStatus.confirmed -> R.color.status_confirmed
                    OrderStatus.shipped -> R.color.status_shipped
                    OrderStatus.delivered -> R.color.status_delivered
                    OrderStatus.cancelled -> R.color.status_cancelled
                }
                
                tvOrderStatus.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, statusColor)
                )
                
                root.setOnClickListener {
                    onOrderClick(order)
                }
            }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
