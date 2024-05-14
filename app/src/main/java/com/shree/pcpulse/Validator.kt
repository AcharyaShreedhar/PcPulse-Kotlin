package com.shree.pcpulse

class Validator {

    // Required field validation
    fun isFieldEmpty(text: String): Boolean {
        return text.isEmpty()
    }

    // Regex validation
    fun isValidRegex(text: String, regex: String): Boolean {
        return text.matches(Regex(regex))
    }
}
