package com.example.timecrafted.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.timecrafted.R
import com.example.timecrafted.databinding.ActivityMainBinding
import com.example.timecrafted.ui.auth.loginScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🧭 Bottom navigation setup
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        val openFragment = intent.getStringExtra("openFragment")
        if (openFragment == "wishlist") {
            binding.bottomNavigation.selectedItemId = R.id.nav_wishlist
        }

//        // Check if an intent requests opening ProfileFragment
//        val openFragment2 = intent.getStringExtra("openFragment")
//        if (openFragment2 == "profile") {
//            binding.bottomNavigation.selectedItemId = R.id.nav_profile
//        }
    }
}
