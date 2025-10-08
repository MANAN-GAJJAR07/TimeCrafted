package com.example.timecrafted.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecrafted.databinding.ActivityUploadImgSuceessfullyBinding
import com.example.timecrafted.ui.main.MainActivity

class uploadImgSuceessfully : AppCompatActivity() {

    private lateinit var binding: ActivityUploadImgSuceessfullyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImgSuceessfullyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set selected image if available
        val imageUriString = intent.getStringExtra("selectedImageUri")
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.profileImage.findViewById<android.widget.ImageView>(0).setImageURI(imageUri)
        }

        // Back arrow → go back to previous activity
        binding.backArrow.setOnClickListener {
            finish()
        }

        // Save button → return to ProfileFragment
        binding.saveBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "profile")
            startActivity(intent)
            finish()
        }
    }
}
