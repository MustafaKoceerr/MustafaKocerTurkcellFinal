package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.model.Product

@Dao
interface ProductDao {



    @Insert
    suspend fun insertProduct(likedProduct: LikedProduct): Long // sqlite return id
    // todo bbeğenirlerse db'ye ekleyeceğim.

    @Update
    suspend fun updateProduct(likedProduct: LikedProduct): Int // sqlite return id


    @Query("select product from liked_product where userId = :userId and isLiked = 1 ")
    suspend fun favoriteProducts(userId: Int): List<Product>
    // todo beğenilen ürünleri getirir. bunu beğenilen ürünlerde çağır


}