package com.example.timecrafted.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.cloudinary.CloudinaryService
import com.example.timecrafted.data.repository.UserRepository
import com.example.timecrafted.databinding.ActivityEditProfilePhotoBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class editProfilePhoto : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfilePhotoBinding
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1001
    
    private val authRepository by lazy { AuthRepository() }
    private val userRepository by lazy { UserRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load current profile image
        loadCurrentProfileImage(userId)

        // Pick image from gallery
        binding.uploadNewPictureBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Cancel → just finish
        binding.cancelBtn.setOnClickListener {
            finish()
        }

        // Save → upload to Cloudinary and save path to Firebase
        binding.saveBtn.setOnClickListener {
            selectedImageUri?.let { uri ->
                uploadProfileImage(userId, uri)
            } ?: run {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCurrentProfileImage(userId: String) {
        userRepository.getUserProfile(
            userId = userId,
            onSuccess = { profile ->
                profile?.let {
                    if (it.profileImagePath.isNotEmpty()) {
                        val imageUrl = CloudinaryService.getImageUrl(it.profileImagePath)
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.img_profile)
                            .error(R.drawable.img_profile)
                            .into(binding.imageView)
                    }
                }
            },
            onError = { }
        )
    }

    private fun uploadProfileImage(userId: String, imageUri: Uri) {
        binding.saveBtn.isEnabled = false
        binding.saveBtn.text = "Uploading..."

        try {
            // Get file path from URI
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val file = File(cacheDir, "profile_${userId}_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Upload to Cloudinary
            CloudinaryService.uploadImage(
                imagePath = file.absolutePath,
                folder = "timecrafted/profiles",
                onSuccess = { publicId ->
                    // Save public ID to Firebase
                    userRepository.updateUserProfile(
                        userId = userId,
                        updates = mapOf("profileImagePath" to publicId),
                        onSuccess = {
                            Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        },
                        onError = { error ->
                            binding.saveBtn.isEnabled = true
                            binding.saveBtn.text = "Save"
                            Snackbar.make(binding.root, "Failed to save: $error", Snackbar.LENGTH_SHORT).show()
                        }
                    )
                },
                onError = { error ->
                    binding.saveBtn.isEnabled = true
                    binding.saveBtn.text = "Save"
                    Snackbar.make(binding.root, "Upload failed: $error", Snackbar.LENGTH_SHORT).show()
                }
            )
        } catch (e: Exception) {
            binding.saveBtn.isEnabled = true
            binding.saveBtn.text = "Save"
            Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                binding.imageView.setImageURI(it)
            }
        }
    }
}
