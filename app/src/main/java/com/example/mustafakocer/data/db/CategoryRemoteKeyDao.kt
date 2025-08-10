package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mustafakocer.data.model.entity.CategoryRemoteKeyEntity

@Dao
interface CategoryRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<CategoryRemoteKeyEntity>)

    @Query("SELECT * FROM category_remote_keys WHERE productId = :productId AND category = :category")
    suspend fun getRemoteKeyForProductId(productId: Int, category: String): CategoryRemoteKeyEntity?

    @Query("DELETE FROM category_remote_keys WHERE category = :category")
    suspend fun clearRemoteKeysByCategory(category: String)
}