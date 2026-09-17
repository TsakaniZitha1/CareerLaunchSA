package com.example.careerlaunchsa

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.careerlaunchsa.data.AppDatabase
import com.example.careerlaunchsa.databinding.ActivitySettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class activity_settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClearCache.setOnClickListener {
            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch(Dispatchers.IO) {
                db.clearAllTables()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@activity_settings, "Offline cache cleared", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
