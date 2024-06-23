package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basic_user")
data class BasicUser(
    @PrimaryKey(autoGenerate = false)
    val localId: Int=1,
    val id: Int
)
// ihtiyacım olan değerleri, databaseye yazdırmak için,
// user id, user name gibi
// aynı anda sadece 1 tane user tutulabilir
// todo ihtiyacın olan parametreleri user sınıfından belirle ve seç