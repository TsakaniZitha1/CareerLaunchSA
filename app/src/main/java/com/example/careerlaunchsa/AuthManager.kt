package com.example.careerlaunchsa.auth

import com.google.firebase.auth.FirebaseAuth

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.localizedMessage)
            }
    }

    fun loginUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.localizedMessage)
            }
    }

    fun logout() {
        auth.signOut()
    }
}