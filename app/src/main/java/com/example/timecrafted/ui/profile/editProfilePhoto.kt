package com.example.timecrafted.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.databinding.ActivityEditProfilePhotoBinding

class editProfilePhoto : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfilePhotoBinding
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Save → return selected image URI to ProfileFragment
        binding.saveBtn.setOnClickListener {
            selectedImageUri?.let {
                val intent = Intent()
                intent.putExtra("selectedImageUri", it.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imageView.setImageURI(selectedImageUri)
        }
    }
}
