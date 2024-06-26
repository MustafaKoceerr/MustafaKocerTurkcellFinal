package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

public const val LOCAL_ID = 1
@Entity(tableName = "basic_user_info")
data class BasicUserInfo(
    val id: Long?=null,
    val username: String?=null,
    val email: String?=null,
    val firstName: String?=null,
    val lastName: String?=null,
    val gender: String,
    val image: String?=null,
    @PrimaryKey(autoGenerate = false)
    val localId: Int= LOCAL_ID,
    // aynı anda bir user tutmasını istiyorum

    )
