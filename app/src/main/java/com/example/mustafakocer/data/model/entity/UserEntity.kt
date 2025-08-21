package com.example.mustafakocer.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the currently logged-in user, cached in the local Room database.
 * This table is designed to hold only a single user entry at any time.
 */
@Entity(tableName = "current_user")
data class UserEntity(
    @PrimaryKey val id: Int,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val image: String,
    val phone: String,
    val birthDate: String,
    val age: Int,
    val gender: String
)