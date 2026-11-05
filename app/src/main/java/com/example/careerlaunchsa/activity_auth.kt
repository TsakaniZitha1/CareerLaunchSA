package com.example.careerlaunchsa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.careerlaunchsa.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class activity_auth : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authManager = AuthManager()
    private var isSignUpMode = false

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // 1. Configure Google Sign-In options with safety check for client ID
        val webClientId = try {
            getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            "" // Fallback if google-services.json hasn't generated the string yet
        }

        val gso = if (webClientId.isNotEmpty()) {
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
        } else {
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        }

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        updateUiMode()

        // Toggle Sign In / Register UI state
        binding.tvToggleMode.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUiMode()
        }

        // Email / Password Authentication Button
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

        // 2. Google Single Sign-On (SSO) Button Listener
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    // Google Sign-In Result Launcher
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                if (account.idToken != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUiMode() {
        if (isSignUpMode) {
            binding.tilFullName.visibility = View.VISIBLE
            binding.btnAuthAction.text = "Register"
            binding.tvToggleMode.text = "Already have an account? Sign In"
        } else {
            binding.tilFullName.visibility = View.GONE
            binding.btnAuthAction.text = "Sign In"
            binding.tvToggleMode.text = "Don't have an account? Register"
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}