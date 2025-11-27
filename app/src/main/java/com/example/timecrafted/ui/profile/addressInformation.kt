package com.example.timecrafted.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.model.Address
import com.example.timecrafted.data.repository.UserRepository
import com.example.timecrafted.ui.profile.adapter.AddressAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ValueEventListener

class addressInformation : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var adapter: AddressAdapter
    private val authRepository by lazy { AuthRepository() }
    private val userRepository by lazy { UserRepository() }
    private var addressListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_information)

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            finish()
            return
        }

        // Initialize views
        val backArrow = findViewById<ImageView>(R.id.backBtn)
        val addAddressContainer = findViewById<LinearLayout>(R.id.addAddress)
        recyclerView = findViewById(R.id.rvAddresses)
        emptyState = findViewById(R.id.emptyState)

        backArrow.setOnClickListener {
            finish()
        }

        addAddressContainer.setOnClickListener {
            val intent = Intent(this, addAddress::class.java)
            startActivity(intent)
        }

        setupRecyclerView(userId)
        loadAddresses(userId)
    }

    private fun setupRecyclerView(userId: String) {
        adapter = AddressAdapter(
            onEditClick = { address ->
                val intent = Intent(this, editAddress::class.java)
                intent.putExtra("addressId", address.id)
                startActivity(intent)
            },
            onDeleteClick = { address ->
                userRepository.deleteAddress(
                    userId = userId,
                    addressId = address.id,
                    onSuccess = {
                        Snackbar.make(findViewById(android.R.id.content), "Address deleted", Snackbar.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Snackbar.make(findViewById(android.R.id.content), "Failed to delete: $error", Snackbar.LENGTH_SHORT).show()
                    }
                )
            },
            onSetDefaultClick = { address ->
                setAsDefaultAddress(userId, address)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadAddresses(userId: String) {
        addressListener = userRepository.getUserAddresses(
            userId,
            onResult = { addresses ->
                adapter.submitList(addresses)
                emptyState.isVisible = addresses.isEmpty()
                recyclerView.isVisible = addresses.isNotEmpty()
            },
            onError = { error ->
                Snackbar.make(findViewById(android.R.id.content), "Failed to load addresses: $error", Snackbar.LENGTH_SHORT).show()
            }
        )
    }

    private fun setAsDefaultAddress(userId: String, address: Address) {
        // First, get all addresses and unset current default
        val listener = userRepository.getUserAddresses(
            userId,
            onResult = { addresses ->
                // Update all addresses to set isDefault to false, then set the selected one to true
                addresses.forEach { addr ->
                    if (addr.isDefault && addr.id != address.id) {
                        userRepository.updateAddress(
                            userId = userId,
                            addressId = addr.id,
                            address = addr.copy(isDefault = false),
                            onSuccess = {},
                            onError = {}
                        )
                    }
                }
                
                // Set the selected address as default
                userRepository.updateAddress(
                    userId = userId,
                    addressId = address.id,
                    address = address.copy(isDefault = true),
                    onSuccess = {
                        Snackbar.make(findViewById(android.R.id.content), "Default address updated", Snackbar.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Snackbar.make(findViewById(android.R.id.content), "Failed to update: $error", Snackbar.LENGTH_SHORT).show()
                    }
                )
            },
            onError = {}
        )
    }

    override fun onResume() {
        super.onResume()
        val userId = authRepository.getCurrentUserId()
        userId?.let { loadAddresses(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        addressListener?.let { listener ->
            val userId = authRepository.getCurrentUserId()
            userId?.let { userRepository.removeAddressListener(it, listener) }
        }
    }

}
//override fun onResume() {
//    super.onResume()
//    Log.d("AddressInfo", "Activity resumed")
//    // Ensure activity stays active
//}
//
//override fun onPause() {
//    super.onPause()
//    Log.d("AddressInfo", "Activity paused")
//    // Don't finish here
//}
//
//override fun onDestroy() {
//    super.onDestroy()
//    Log.d("AddressInfo", "Activity destroyed")
//}



