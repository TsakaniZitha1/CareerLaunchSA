package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.careerlaunchsa.auth.AuthManager
import com.example.careerlaunchsa.databinding.ActivityAuthBinding

class activity_auth : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authManager = AuthManager()
    private var isSignUpMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUiMode()

        binding.tvToggleMode.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUiMode()
        }

        binding.btnAuthAction.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isSignUpMode) {
                authManager.registerUser(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    } else {
                        Toast.makeText(this, "Registration Failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                authManager.loginUser(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    } else {
                        Toast.makeText(this, "Login Failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUiMode() {
        if (isSignUpMode) {
            binding.tilFullName.visibility = android.view.View.VISIBLE
            binding.btnAuthAction.text = "Register"
            binding.tvToggleMode.text = "Already have an account? Sign In"
        } else {
            binding.tilFullName.visibility = android.view.View.GONE
            binding.btnAuthAction.text = "Sign In"
            binding.tvToggleMode.text = "Don't have an account? Register"
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, activity_main::class.java))
        finish()
    }
}
