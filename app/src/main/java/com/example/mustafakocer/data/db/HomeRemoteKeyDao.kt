package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mustafakocer.data.model.entity.HomeRemoteKeyEntity

/**
 * Data Access Object for home screen remote keys.
 * Manages pagination state for the Paging 3 library for the main product list.
 */
@Dao
interface HomeRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<HomeRemoteKeyEntity>)

    @Query("SELECT * FROM remote_keys WHERE productId = :productId")
    suspend fun getRemoteKeyForProductId(productId: Int): HomeRemoteKeyEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}