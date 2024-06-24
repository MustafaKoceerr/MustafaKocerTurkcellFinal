package com.example.mustafakocer.data.repository

import androidx.lifecycle.LiveData
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.db.entity.BasicUserInfo
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.data.network.IDummyApi
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val db: AppDatabase
) {

    suspend fun insertProduct(likedProduct: LikedProduct): Long {
        return db.createProductDao().insertProduct(likedProduct)
    }

    suspend fun updateProduct(likedProduct: LikedProduct): Int {
        return db.createProductDao().updateProduct(likedProduct)
    }

    suspend fun getFavoriteProducts(userId: Int): List<Product> {
        return db.createProductDao().favoriteProducts(userId)
    }


    suspend fun insertUser(localUser: BasicUserInfo): Long {
        return db.createUserDao().insertUser(localUser)
    }

    suspend fun deleteUser(localUser: BasicUserInfo): Int {
        return db.createUserDao().deleteUser(localUser)
    }

    suspend fun getUser(localId: Long): BasicUserInfo {
        return db.createUserDao().getUser()
    }

}