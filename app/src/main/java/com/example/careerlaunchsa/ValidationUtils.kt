package com.example.careerlaunchsa.util

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return email.contains("@") && email.contains(".")
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}