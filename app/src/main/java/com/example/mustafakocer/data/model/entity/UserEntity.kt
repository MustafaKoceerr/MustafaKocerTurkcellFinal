package com.example.mustafakocer.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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