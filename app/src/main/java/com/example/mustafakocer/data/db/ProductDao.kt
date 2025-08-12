package com.example.mustafakocer.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mustafakocer.data.model.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("SELECT * FROM products")
    fun getProductsPagingSource(): PagingSource<Int, ProductEntity>

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    @Query("SELECT * FROM products WHERE category = :categoryName")
    fun getProductsByCategoryPagingSource(categoryName: String): PagingSource<Int, ProductEntity>

    @Query("DELETE FROM products WHERE category = :categoryName")
    suspend fun clearProductsByCategory(categoryName: String)

    @Query("SELECT * FROM products WHERE id IN (:ids)")
    fun getProductsByIds(ids: List<Int>): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY id DESC LIMIT 1")
    suspend fun getLastProduct(): ProductEntity?
}