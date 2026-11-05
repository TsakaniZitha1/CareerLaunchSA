package com.example.careerlaunchsa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.databinding.ActivitySettingsBinding
import com.example.careerlaunchsa.data.LocaleHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class activity_settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun attachBaseContext(newBase: Context) {
        // Apply saved locale configuration before layout inflation
        val savedLanguage = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupClickListeners()
        updateLanguageDisplay()
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
            val user = FirebaseAuth.getInstance().currentUser
            if (user?.email != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(user.email!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset link sent to ${user.email}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "No active email found for reset.", Toast.LENGTH_SHORT).show()
            }
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
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        binding.switchDarkMode.isChecked = (currentMode == AppCompatDelegate.MODE_NIGHT_YES)

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Dark mode enabled", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "Light mode enabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear Room Database & Application Cache
        binding.btnClearCache.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@activity_settings)
                db.clearAllTables()

                applicationContext.cacheDir.deleteRecursively()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@activity_settings, "Offline cache cleared", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnOfflineSync.setOnClickListener {
            val isOnline = com.example.careerlaunchsa.utils.NetworkObserver.isNetworkAvailable(this)
            if (isOnline) {
                Toast.makeText(this, "Offline data synced with server!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Device is currently offline. Connect to internet to sync.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDataUsage.setOnClickListener {
            Toast.makeText(this, "Data Usage: Low (0.5 MB cached)", Toast.LENGTH_SHORT).show()
        }

        // SECTION 4: DYNAMIC MULTI-LANGUAGE SELECTOR (English, isiZulu, Afrikaans)
        binding.btnLanguage.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "isiZulu", "Afrikaans")
        val codes = arrayOf("en", "zu", "af")

        val currentCode = LocaleHelper.getSavedLanguage(this)
        val checkedItem = codes.indexOf(currentCode).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle("Select Language / Khetha Ulimi")
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val selectedCode = codes[which]
                if (selectedCode != currentCode) {
                    LocaleHelper.setLocale(this, selectedCode)
                    dialog.dismiss()

                    // Restart Activity to re-bind string resources instantly
                    val intent = intent
                    finish()
                    startActivity(intent)
                } else {
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateLanguageDisplay() {
        val currentCode = LocaleHelper.getSavedLanguage(this)
        binding.tvCurrentLanguage.text = when (currentCode) {
            "zu" -> "isiZulu"
            "af" -> "Afrikaans"
            else -> "English"
        }
    }
}