package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.model.Product

@Dao
interface ProductDao {



    @Insert
    suspend fun insertProduct(likedProduct: LikedProduct): Long // sqlite return id
    // todo bbeğenirlerse db'ye ekleyeceğim.

 /*
    @Update
    suspend fun updateProduct(likedProduct: LikedProduct): Int // sqlite return id

  */
    @Query("UPDATE liked_product SET isLiked = CASE WHEN isLiked == 0 THEN 1 ELSE 0 END WHERE userId = :userId AND product = :product")
    suspend fun toggleLikedStatus(userId: Int, product: Product): Int
    // 0 dönerse failed, 0'dan farklı dönerse succes

    @Query("select * from liked_product where userId = :userId and isLiked = 1 ")
    suspend fun getAllProducts(userId: Int): List<LikedProduct>
    // todo beğenilen ürünleri getirir. bunu beğenilen ürünlerde çağır


}