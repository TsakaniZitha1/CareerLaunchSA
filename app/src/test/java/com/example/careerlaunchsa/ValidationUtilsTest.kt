package com.example.careerlaunchsa

import com.example.careerlaunchsa.util.ValidationUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun validEmail_returnsTrue() {
        val email = "student@rosebankcollege.co.za"
        assertTrue(ValidationUtils.isValidEmail(email))
    }

    @Test
    fun invalidEmail_missingAtSign_returnsFalse() {
        val email = "studentrosebankcollege.co.za"
        assertFalse(ValidationUtils.isValidEmail(email))
    }

    @Test
    fun emptyEmail_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun validPassword_lengthSixOrMore_returnsTrue() {
        assertTrue(ValidationUtils.isValidPassword("Secure123"))
    }

    @Test
    fun invalidPassword_tooShort_returnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("123"))
    }
}