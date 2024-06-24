package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

public const val LOCAL_ID = 1
@Entity(tableName = "basic_user_info")
data class BasicUserInfo(
    @PrimaryKey(autoGenerate = false)
    val localId: Int?= LOCAL_ID,
    // aynı anda bir user tutmasını istiyorum
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
)
