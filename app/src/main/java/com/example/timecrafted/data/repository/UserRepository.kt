package com.example.timecrafted.data.repository

import com.example.timecrafted.data.model.Address
import com.example.timecrafted.data.model.UserProfile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    fun saveUserProfile(
        userId: String,
        profile: UserProfile,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("users").child(userId).setValue(profile.copy(uid = userId, updatedAt = System.currentTimeMillis()))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to save profile") }
    }

    fun getUserProfile(
        userId: String,
        onSuccess: (UserProfile?) -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("users").child(userId).get()
            .addOnSuccessListener { snapshot ->
                val profile = snapshot.getValue(UserProfile::class.java)
                onSuccess(profile)
            }
            .addOnFailureListener { onError(it.message ?: "Failed to get profile") }
    }

    fun updateUserProfile(
        userId: String,
        updates: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val updateMap = updates.toMutableMap()
        updateMap["updatedAt"] = System.currentTimeMillis()
        
        database.child("users").child(userId).updateChildren(updateMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update profile") }
    }

    fun saveAddress(
        userId: String,
        address: Address,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val addressRef = database.child("addresses").child(userId).push()
        val addressId = addressRef.key ?: ""
        val addressWithId = address.copy(id = addressId, userId = userId, updatedAt = System.currentTimeMillis())
        
        addressRef.setValue(addressWithId)
            .addOnSuccessListener { onSuccess(addressId) }
            .addOnFailureListener { onError(it.message ?: "Failed to save address") }
    }

    fun updateAddress(
        userId: String,
        addressId: String,
        address: Address,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("addresses").child(userId).child(addressId)
            .setValue(address.copy(id = addressId, userId = userId, updatedAt = System.currentTimeMillis()))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update address") }
    }

    fun deleteAddress(
        userId: String,
        addressId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.child("addresses").child(userId).child(addressId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to delete address") }
    }

    fun getUserAddresses(
        userId: String,
        onResult: (List<Address>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {
        val reference = database.child("addresses").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val addresses = snapshot.children.mapNotNull { child ->
                    child.getValue(Address::class.java)?.copy(id = child.key ?: "")
                }
                onResult(addresses)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }
        reference.addValueEventListener(listener)
        return listener
    }

    fun removeAddressListener(userId: String, listener: ValueEventListener) {
        database.child("addresses").child(userId).removeEventListener(listener)
    }
}

