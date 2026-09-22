package com.example.careerlaunchsa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.careerlaunchsa.databinding.ActivityCvBinding

class CvActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCvBinding
    private var selectedCvUri: Uri? = null

    private val cvPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedCvUri = result.data?.data
            binding.tvCvStatus.text = "CV Selected: ${selectedCvUri?.lastPathSegment ?: "Document.pdf"}"
            Toast.makeText(this, "CV Document Attached!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackNavigation()

        binding.btnUploadCv.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "application/msword"))
            }
            cvPickerLauncher.launch(intent)
        }
    }

    private fun setupBackNavigation() {
        // 1. UI Back Button click in top header
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. Physical back button / Android gesture back handler
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}