package com.example.timecrafted.ui.profile

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.model.Address
import com.example.timecrafted.data.repository.UserRepository

class editAddress : AppCompatActivity() {

    private lateinit var fullNameEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var addressLine1Edit: EditText
    private lateinit var addressLine2Edit: EditText
    private lateinit var cityEdit: EditText
    private lateinit var stateEdit: EditText
    private lateinit var zipCodeEdit: EditText
    private lateinit var countryEdit: EditText
    private lateinit var defaultSwitch: Switch

    private val authRepository by lazy { AuthRepository() }
    private val userRepository by lazy { UserRepository() }
    private var currentAddress: Address? = null
    private var addressId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_address)

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Toast.makeText(this, "Please login to edit address", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get address ID from intent
        addressId = intent.getStringExtra("addressId") ?: ""
        if (addressId.isEmpty()) {
            Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        val backArrow = findViewById<ImageView>(R.id.rvdzbgxq6ll)
        val cancelButton = findViewById<Button>(R.id.rj5cn2mp6fh)
        val saveChangesButton = findViewById<Button>(R.id.r3ygx9ogp9xs)
        val deleteAddressButton = findViewById<Button>(R.id.r63e71vxihl)

        fullNameEdit = findViewById(R.id.fullNameEdit)
        phoneNumberEdit = findViewById(R.id.phoneNumberEdit)
        addressLine1Edit = findViewById(R.id.addressLine1Edit)
        addressLine2Edit = findViewById(R.id.addressLine2Edit)
        cityEdit = findViewById(R.id.cityEdit)
        stateEdit = findViewById(R.id.stateEdit)
        zipCodeEdit = findViewById(R.id.zipCodeEdit)
        countryEdit = findViewById(R.id.countryEdit)
        defaultSwitch = findViewById(R.id.switch1)

        backArrow.setOnClickListener {
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }

        saveChangesButton.setOnClickListener {
            if (validateInput()) {
                updateAddress(userId)
            }
        }

        deleteAddressButton.setOnClickListener {
            showDeleteConfirmation(userId)
        }

        // Load address data
        loadAddress(userId)
    }

    private fun loadAddress(userId: String) {
        val listener = userRepository.getUserAddresses(
            userId,
            onResult = { addresses ->
                currentAddress = addresses.find { it.id == addressId }
                currentAddress?.let { address ->
                    populateFields(address)
                } ?: run {
                    Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            onError = { error ->
                Toast.makeText(this, "Failed to load address: $error", Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }

    private fun populateFields(address: Address) {
        fullNameEdit.setText(address.fullName)
        phoneNumberEdit.setText(address.phoneNumber)
        addressLine1Edit.setText(address.addressLine1)
        addressLine2Edit.setText(address.addressLine2)
        cityEdit.setText(address.city)
        stateEdit.setText(address.state)
        zipCodeEdit.setText(address.zipCode)
        countryEdit.setText(address.country)
        defaultSwitch.isChecked = address.isDefault
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

    private fun updateAddress(userId: String) {
        currentAddress?.let { oldAddress ->
            val updatedAddress = oldAddress.copy(
                fullName = fullNameEdit.text.toString().trim(),
                phoneNumber = phoneNumberEdit.text.toString().trim(),
                addressLine1 = addressLine1Edit.text.toString().trim(),
                addressLine2 = addressLine2Edit.text.toString().trim(),
                city = cityEdit.text.toString().trim(),
                state = stateEdit.text.toString().trim(),
                zipCode = zipCodeEdit.text.toString().trim(),
                country = countryEdit.text.toString().trim().ifEmpty { "India" },
                isDefault = defaultSwitch.isChecked
            )

            userRepository.updateAddress(
                userId = userId,
                addressId = addressId,
                address = updatedAddress,
                onSuccess = {
                    Toast.makeText(this, "Address updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, "Failed to update address: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showDeleteConfirmation(userId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Address")
            .setMessage("Are you sure you want to delete this address?")
            .setPositiveButton("Delete") { _, _ ->
                deleteAddress(userId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAddress(userId: String) {
        userRepository.deleteAddress(
            userId = userId,
            addressId = addressId,
            onSuccess = {
                Toast.makeText(this, "Address deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                Toast.makeText(this, "Failed to delete address: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        Log.d("EditAddress", "Activity resumed")
        // Ensure activity stays active
    }

    override fun onPause() {
        super.onPause()
        Log.d("EditAddress", "Activity paused")
        // Don't finish here
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("EditAddress", "Activity destroyed")
    }
}