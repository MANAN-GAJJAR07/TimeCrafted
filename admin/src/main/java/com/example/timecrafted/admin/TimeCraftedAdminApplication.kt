package com.example.timecrafted.admin

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp

class TimeCraftedAdminApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Cloudinary
        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = "your_cloud_name"
            config["api_key"] = "your_api_key"
            config["api_secret"] = "your_api_secret"
            MediaManager.init(this, config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
