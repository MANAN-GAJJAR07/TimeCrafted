package com.example.timecrafted.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.databinding.ItemCartBinding
import com.example.timecrafted.ui.product.model.CartItem

class CartItemAdapter(
    private val onQuantityChanged: (String, Int) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    private val cartList = mutableListOf<CartItem>()

    inner class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.binding.apply {
            Glide.with(imgProduct)
                .load(item.imageUrl.ifBlank { null })
                .placeholder(R.drawable.product_img)
                .error(R.drawable.product_img)
                .into(imgProduct)

            tvProductName.text = item.name
            tvQuantity.text = item.quantity.toString()
            
            // Calculate total price for this item
            val totalPrice = item.price * item.quantity
            val currencySymbol = item.currency
            val format = java.text.NumberFormat.getNumberInstance(java.util.Locale.getDefault())
            format.minimumFractionDigits = 0
            format.maximumFractionDigits = 2
            tvProductPrice.text = "$currencySymbol${format.format(totalPrice)}"

            // Quantity controls
            btnIncrease.setOnClickListener {
                val newQuantity = item.quantity + 1
                onQuantityChanged(item.id, newQuantity)
            }

            btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    val newQuantity = item.quantity - 1
                    onQuantityChanged(item.id, newQuantity)
                }
            }

            // Delete button
            btnDelete.setOnClickListener {
                onDelete(item.id)
            }
        }
    }

    override fun getItemCount(): Int = cartList.size

    fun submitList(items: List<CartItem>) {
        cartList.clear()
        cartList.addAll(items)
        notifyDataSetChanged()
    }
}
