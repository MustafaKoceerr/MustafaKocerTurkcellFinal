package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mustafakocer.data.model.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    /**
     * Mevcut kullanıcıyı veritabanına ekler. Eğer zaten bir kullanıcı varsa,
     * üzerine yazar. Bu, tablonun her zaman tek bir kullanıcı içermesini sağlar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(user: UserEntity)

    /**
     * Mevcut kullanıcıyı bir Flow olarak döndürür.
     * Bu, UI'ın kullanıcı verilerindeki değişiklikleri reaktif olarak dinlemesini sağlar.
     * 'LIMIT 1' ifadesi, her zaman en fazla bir sonuç döndürülmesini garanti eder.
     */
    @Query("SELECT * FROM current_user LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    /**
     * 'current_user' tablosundaki tüm verileri siler.
     * Bu, kullanıcı çıkış yaptığında çağrılır.
     */
    @Query("DELETE FROM current_user")
    suspend fun clearUser()
}