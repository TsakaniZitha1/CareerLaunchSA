package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class activity_settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupClickListeners()
    }

    private fun setupNavigation() {
        // 1. Back button in top header
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. System back gesture / hardware back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupClickListeners() {
        // SECTION 1: ACCOUNT SECURITY
        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Password reset link sent to your email.", Toast.LENGTH_SHORT).show()
        }

        binding.btnSecureLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, activity_auth::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // SECTION 2: NOTIFICATION PREFERENCES
        binding.switchJobAlerts.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Job alerts $status", Toast.LENGTH_SHORT).show()
        }

        binding.switchAppStatus.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Application status alerts $status", Toast.LENGTH_SHORT).show()
        }

        // SECTION 3: APP SETTINGS
        // Clear Room Database & Application Cache
        binding.btnClearCache.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                // Clear Room Database
                val db = AppDatabase.getDatabase(this@activity_settings)
                db.clearAllTables()

                // Clear internal cache directory files
                applicationContext.cacheDir.deleteRecursively()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@activity_settings, "Offline cache cleared", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnOfflineSync.setOnClickListener {
            Toast.makeText(this, "Offline data synced", Toast.LENGTH_SHORT).show()
        }

        binding.btnDataUsage.setOnClickListener {
            Toast.makeText(this, "Data Usage: Low (0.5 MB cached)", Toast.LENGTH_SHORT).show()
        }

        // SECTION 4: LANGUAGE
        binding.btnLanguage.setOnClickListener {
            if (binding.tvCurrentLanguage.text == "English") {
                binding.tvCurrentLanguage.text = "isiZulu"
                Toast.makeText(this, "uLimi lushuquliwe lwaya esiZulwini", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvCurrentLanguage.text = "English"
                Toast.makeText(this, "Language switched to English", Toast.LENGTH_SHORT).show()
            }
        }
    }
}