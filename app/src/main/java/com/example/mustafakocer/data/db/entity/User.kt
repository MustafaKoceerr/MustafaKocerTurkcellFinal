package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "user"
)
data class User (
    @PrimaryKey(autoGenerate = false)
    val localUserId: Int=1, // local id, I want to store just one user who is logged in
    val id: Long,
    val firstName: String,
    val lastName: String,
    val maidenName: String,
    val age: Long,
    val gender: String,
    val email: String,
    val phone: String,
    val username: String,
    val password: String,
    val birthDate: String,
    val image: String,
    val bloodGroup: String,
    val height: Double,
    val weight: Double,
    val eyeColor: String,
    val hair: Hair,
    val ip: String,
    val address: Address,
    val macAddress: String,
    val university: String,
    val bank: Bank,
    val company: Company,
    val ein: String,
    val ssn: String,
    val userAgent: String,
    val crypto: Crypto,
    val role: String
)

data class Address (
    val address: String,
    val city: String,
    val state: String,
    val stateCode: String,
    val postalCode: String,
    val coordinates: Coordinates,
    val country: String
)

data class Coordinates (
    val lat: Double,
    val lng: Double
)

data class Bank (
    val cardExpire: String,
    val cardNumber: String,
    val cardType: String,
    val currency: String,
    val iban: String
)

data class Company (
    val department: String,
    val name: String,
    val title: String,
    val address: Address
)

data class Crypto (
    val coin: String,
    val wallet: String,
    val network: String
)

data class Hair (
    val color: String,
    val type: String
)
