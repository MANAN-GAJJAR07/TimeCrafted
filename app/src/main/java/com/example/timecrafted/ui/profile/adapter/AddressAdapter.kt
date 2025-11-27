package com.example.timecrafted.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.databinding.ItemAddressBinding
import com.example.timecrafted.data.model.Address

class AddressAdapter(
    private val onEditClick: (Address) -> Unit,
    private val onDeleteClick: (Address) -> Unit,
    private val onSetDefaultClick: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private val addressList = mutableListOf<Address>()

    inner class AddressViewHolder(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.binding.apply {
            tvAddressName.text = address.fullName
            tvAddressPhone.text = "Phone: ${address.phoneNumber}"
            
            val addressText = buildString {
                append(address.addressLine1)
                if (address.addressLine2.isNotEmpty()) {
                    append(", ${address.addressLine2}")
                }
                append(", ${address.city}, ${address.state} ${address.zipCode}")
                if (address.country.isNotEmpty() && address.country != "India") {
                    append(", ${address.country}")
                }
            }
            tvAddressFull.text = addressText

            // Show default badge if this is the default address
            if (address.isDefault) {
                tvDefaultBadge.visibility = android.view.View.VISIBLE
            } else {
                tvDefaultBadge.visibility = android.view.View.GONE
            }

            // Edit button
            btnEdit.setOnClickListener {
                onEditClick(address)
            }

            // Delete button
            btnDelete.setOnClickListener {
                onDeleteClick(address)
            }

            // Set as default button (only show if not already default)
            if (address.isDefault) {
                btnSetDefault.visibility = android.view.View.GONE
            } else {
                btnSetDefault.visibility = android.view.View.VISIBLE
                btnSetDefault.setOnClickListener {
                    onSetDefaultClick(address)
                }
            }
        }
    }

    override fun getItemCount(): Int = addressList.size

    fun submitList(addresses: List<Address>) {
        addressList.clear()
        addressList.addAll(addresses)
        notifyDataSetChanged()
    }
}

