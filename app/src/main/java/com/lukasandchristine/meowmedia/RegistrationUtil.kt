package com.lukasandchristine.meowmedia

import java.util.regex.Pattern

object RegistrationUtil {
    private var existingUsers = listOf("admin")
    private var exitingEmails = listOf("admin@admin.net")

    // isn't empty
    // isn't already taken
    // minimum number of characters is 3
    fun validateUsername(username: String) : Boolean {
        if(username.isEmpty()) {
            return false
        }
        if(existingUsers.contains(username)) {
            return false
        }
        return username.length >= 3
    }

    // make sure meets security requirements (deprecated ones that are still used everywhere)
    // min length 8 chars
    // at least one digit
    // at least one capital letter
    // both passwords match
    // not empty
    fun validatePassword(password: String, confirmPassword: String) : Boolean {
        if(password.length < 8 || confirmPassword.length < 8) {
            return false
        }

        password.count { it.isDigit() } > 0
        password.count { it.isUpperCase() } > 0

        if(password != confirmPassword) {
            return false
        }

        return !(password.isEmpty() || confirmPassword.isEmpty())
    }

    // isn't empty
    fun validateName(name: String) : Boolean {
        return name.isNotEmpty()
    }

    // isn't empty
    // make sure the email isn't used
    // make sure it's in the proper email format user@domain.tld
    fun validateEmail(email: String) : Boolean {
        if(email.isEmpty()) {
            return false
        }
        if(exitingEmails.contains(email)) {
            return false
        }
        return Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)
    }
}