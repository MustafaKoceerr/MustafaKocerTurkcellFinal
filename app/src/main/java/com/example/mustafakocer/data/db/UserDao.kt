package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mustafakocer.data.model.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing the current user's data.
 * Ensures that there is only one user entry in the database at any time.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(user: UserEntity)

    @Query("SELECT * FROM current_user LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("DELETE FROM current_user")
    suspend fun clearUser()
}