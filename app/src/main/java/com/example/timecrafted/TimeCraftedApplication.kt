package com.example.timecrafted

import android.app.Application
import com.example.timecrafted.data.cloudinary.CloudinaryService

class TimeCraftedApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Cloudinary
        // Get credentials from strings.xml or BuildConfig
        val cloudName = getString(R.string.cloudinary_cloud_name)
        val apiKey = getString(R.string.cloudinary_api_key)
        val apiSecret = getString(R.string.cloudinary_api_secret)

        // Only initialize if credentials are provided
        if (cloudName.isNotBlank() && apiKey.isNotBlank() && apiSecret.isNotBlank()) {
            CloudinaryService.initialize(
                context = this,
                cloudName = cloudName,
                apiKey = apiKey,
                apiSecret = apiSecret
            )
        }
    }
}

