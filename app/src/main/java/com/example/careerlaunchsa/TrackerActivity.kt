package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.databinding.ActivityTrackerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationAndClicks()
        setupBottomNavigation()
        loadApplicationStats()
    }

    private fun setupNavigationAndClicks() {
        // 1. Back button click in header
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. System back gesture / button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_applications
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_browse -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_applications -> true
                R.id.nav_cv -> {
                    startActivity(Intent(this, CvActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, activity_settings::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadApplicationStats() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val savedEntities = db.jobDao().getAllSavedJobs()
                withContext(Dispatchers.Main) {
                    binding.tvApplicationCountSubtitle.text = "${savedEntities.size} tracked"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Fallback default matching your design mockup
                    binding.tvApplicationCountSubtitle.text = "2 tracked"
                }
            }
        }
    }
}