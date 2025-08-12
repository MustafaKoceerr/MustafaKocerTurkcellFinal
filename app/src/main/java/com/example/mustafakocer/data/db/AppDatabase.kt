package com.example.mustafakocer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mustafakocer.data.model.entity.CategoryRemoteKeyEntity
import com.example.mustafakocer.data.model.entity.ProductEntity
import com.example.mustafakocer.data.model.entity.HomeRemoteKeyEntity
import com.example.mustafakocer.data.model.entity.UserEntity


@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        HomeRemoteKeyEntity::class,
        CategoryRemoteKeyEntity::class,
    ],
    version = 4, // Şema değiştiği için versiyonu artır
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun createProductDao(): ProductDao

    abstract fun createUserDao(): UserDao

    abstract fun createHomeRemoteKeyDao(): HomeRemoteKeyDao

    abstract fun createCategoryRemoteKeyDao(): CategoryRemoteKeyDao

}