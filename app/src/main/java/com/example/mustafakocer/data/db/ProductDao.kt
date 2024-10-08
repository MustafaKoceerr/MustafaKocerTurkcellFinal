package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mustafakocer.data.db.entity.Cart
import com.example.mustafakocer.data.db.entity.LikedProduct

@Dao
interface ProductDao {


    @Insert
    suspend fun insertProduct(likedProduct: LikedProduct): Long // sqlite return id

    // LikedProduct tablosundan belirli bir kullanıcı ve ürün ID'sine göre ürünü silen suspend fonksiyon
    @Query("DELETE FROM liked_product WHERE userId = :userId AND  productId = :productId")
    suspend fun deleteLikedProduct(userId: Int, productId: Int): Int

    @Query("select * from liked_product where userId = :userId")
    suspend fun getAllProducts(userId: Int): List<LikedProduct>

    @Query("select * from liked_product where userId = :userId and productId = :productId ")
    suspend fun getOneProduct(userId: Int, productId:Int): LikedProduct?



    @Insert
    suspend fun insertCart(cart: Cart): Long // sqlite return id

    @Query("DELETE FROM cart_list WHERE userId = :userId AND  id = :productId")
    suspend fun deleteCart(userId: Int, productId: Int): Int

    @Query("select * from cart_list where userId = :userId")
    suspend fun getAllCarts(userId: Int): List<Cart>

    @Query("select * from cart_list where userId = :userId and id = :productId ")
    suspend fun getOneCart(userId: Int, productId:Int): Cart?

    @Update
    suspend fun updateCart(cart: Cart): Int
}