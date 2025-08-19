package com.example.mustafakocer.domain.model

/**
 * A clean model representing a user in the application's UI and domain layers.
 */
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val username: String,
    val gender: String,
    val age: Int,
    val imageUrl: String
) {
    /**
     * A computed property that returns the full name, derived from the current
     * state of firstName and lastName.
     */
    val fullName: String
        get() = "$firstName $lastName".trim()
}