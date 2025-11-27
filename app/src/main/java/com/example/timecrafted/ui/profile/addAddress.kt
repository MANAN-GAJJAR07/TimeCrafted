package com.example.timecrafted.ui.profile

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.model.Address
import com.example.timecrafted.data.repository.UserRepository

class addAddress : AppCompatActivity() {

    private lateinit var fullNameEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var addressLine1Edit: EditText
    private lateinit var addressLine2Edit: EditText
    private lateinit var cityEdit: EditText
    private lateinit var stateEdit: EditText
    private lateinit var zipCodeEdit: EditText

    private val authRepository by lazy { AuthRepository() }
    private val userRepository by lazy { UserRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Toast.makeText(this, "Please login to add address", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        val cancelButton = findViewById<Button>(R.id.cancelBtn)
        val saveAddressButton = findViewById<Button>(R.id.saveAddressBtn)
        val backArrow = findViewById<ImageView>(R.id.backBtn)

        fullNameEdit = findViewById(R.id.r0rz0yiiopp6h)
        phoneNumberEdit = findViewById(R.id.phoneNumberEdit)
        addressLine1Edit = findViewById(R.id.rlver2i7dhy)
        addressLine2Edit = findViewById(R.id.r3c9ej87dheb)
        cityEdit = findViewById(R.id.r5clnwkvwiem)
        stateEdit = findViewById(R.id.r26b34o1nxfc)
        zipCodeEdit = findViewById(R.id.rawe6b9z6cg7)

        cancelButton.setOnClickListener {
            finish()
        }

        backArrow.setOnClickListener {
            finish()
        }

        saveAddressButton.setOnClickListener {
            if (validateInput()) {
                saveAddress(userId)
            }
        }
    }

    private fun validateInput(): Boolean {
        if (fullNameEdit.text.toString().trim().isEmpty()) {
            fullNameEdit.error = "Please enter full name"
            return false
        }
        if (phoneNumberEdit.text.toString().trim().isEmpty()) {
            phoneNumberEdit.error = "Please enter mobile number"
            return false
        }
        if (addressLine1Edit.text.toString().trim().isEmpty()) {
            addressLine1Edit.error = "Please enter address"
            return false
        }
        if (cityEdit.text.toString().trim().isEmpty()) {
            cityEdit.error = "Please enter city"
            return false
        }
        if (stateEdit.text.toString().trim().isEmpty()) {
            stateEdit.error = "Please enter state"
            return false
        }
        if (zipCodeEdit.text.toString().trim().isEmpty()) {
            zipCodeEdit.error = "Please enter zip code"
            return false
        }
        return true
    }

    private fun saveAddress(userId: String) {
        val address = Address(
            userId = userId,
            fullName = fullNameEdit.text.toString().trim(),
            phoneNumber = phoneNumberEdit.text.toString().trim(),
            addressLine1 = addressLine1Edit.text.toString().trim(),
            addressLine2 = addressLine2Edit.text.toString().trim(),
            city = cityEdit.text.toString().trim(),
            state = stateEdit.text.toString().trim(),
            zipCode = zipCodeEdit.text.toString().trim(),
            country = "India",
            isDefault = false // First address will be set as default if no default exists
        )

        userRepository.saveAddress(
            userId = userId,
            address = address,
            onSuccess = { addressId ->
                Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                Toast.makeText(this, "Failed to save address: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        Log.d("AddAddress", "Activity resumed")
        // Ensure activity stays active
    }

    override fun onPause() {
        super.onPause()
        Log.d("AddAddress", "Activity paused")
        // Don't finish here
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AddAddress", "Activity destroyed")
    }
}