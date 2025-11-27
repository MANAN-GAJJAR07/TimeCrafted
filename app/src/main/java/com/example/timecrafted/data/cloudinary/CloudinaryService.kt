package com.example.timecrafted.data.cloudinary

import android.content.Context
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.util.HashMap

object CloudinaryService {
    private var isInitialized = false

    fun initialize(context: Context, cloudName: String, apiKey: String, apiSecret: String) {
        if (isInitialized) return

        val config = HashMap<String, String>()
        config["cloud_name"] = cloudName
        config["api_key"] = apiKey
        config["api_secret"] = apiSecret

        MediaManager.init(context, config)
        isInitialized = true
    }

    /**
     * Get Cloudinary URL for an image path
     * Supports:
     * - Full Cloudinary URLs (returns as-is)
     * - Cloudinary public IDs (e.g., "products/prod001")
     * - HTTP/HTTPS URLs (returns as-is)
     */
    fun getImageUrl(imagePath: String): String {
        if (imagePath.isBlank()) return ""
        
        // If already a full URL, return as-is
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath
        }

        // If it's a Cloudinary URL format, return as-is
        if (imagePath.contains("cloudinary.com")) {
            return imagePath
        }

        // Otherwise, treat as Cloudinary public ID and build URL
        // Format: https://res.cloudinary.com/{cloud_name}/image/upload/{public_id}
        val cloudName = MediaManager.get().cloudinary.config.cloudName
        return "https://res.cloudinary.com/$cloudName/image/upload/$imagePath"
    }

    /**
     * Upload image to Cloudinary
     */
    fun uploadImage(
        imagePath: String,
        folder: String = "timecrafted",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ): String {
        val requestId = MediaManager.get().upload(imagePath)
            .option("folder", folder)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    // Upload started
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Upload progress
                }

                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    val publicId = resultData["public_id"] as? String ?: ""
                    onSuccess(publicId)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    onError(error.description)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    onError(error.description)
                }
            })
            .dispatch()

        return requestId
    }
}

