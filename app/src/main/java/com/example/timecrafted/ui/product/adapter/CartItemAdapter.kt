package com.example.timecrafted.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.databinding.ItemCartBinding

data class CartItem(
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageRes: Int
)

class CartItemAdapter(private val cartList: List<CartItem>) :
    RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.binding.imgProduct.setImageResource(item.imageRes)
        holder.binding.tvProductName.text = item.name
        holder.binding.tvProductQuantity.text = "Quantity: ${item.quantity}"
        holder.binding.tvProductPrice.text = "$${item.price}"
    }

    override fun getItemCount(): Int = cartList.size
}
