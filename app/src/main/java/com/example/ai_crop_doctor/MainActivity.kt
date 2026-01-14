package com.example.ai_crop_doctor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ai_crop_doctor.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Main Activity - Container for all fragments with Bottom Navigation
 *
 * Handles navigation between Home, History, and Map screens
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("MainActivity created")

        setupNavigation()
    }

    /**
     * Setup Navigation Component with Bottom Navigation View
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // Setup bottom navigation with nav controller
        binding.bottomNavigation.setupWithNavController(navController)

        Timber.d("Navigation setup complete")
    }
}
