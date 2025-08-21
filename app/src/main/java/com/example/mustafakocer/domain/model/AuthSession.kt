package com.example.mustafakocer.domain.model

/**
 * Represents the essential user session data after a successful login.
 * This is a pure domain model, free from any data layer dependencies.
 */
data class AuthSession(
    val token: String,
    val userId: Int
)