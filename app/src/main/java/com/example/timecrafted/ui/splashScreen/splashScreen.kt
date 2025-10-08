package com.example.timecrafted.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timecrafted.R
import com.example.timecrafted.ui.auth.gotoScreen
import com.example.timecrafted.ui.auth.registerScreen
import kotlin.jvm.java

class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(getMainLooper()).postDelayed({
            val intent = Intent(this, gotoScreen::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}