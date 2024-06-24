package com.example.mustafakocer.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mustafakocer.data.db.entity.BasicUserInfo
import com.example.mustafakocer.data.db.entity.LOCAL_ID
import com.example.mustafakocer.data.model.Product

@Dao
interface UserDao {

    // Giriş yapan kullanıcıya hoş geldin demek için burada user'ı tutuyorum

    @Insert
    suspend fun insertUser( localUser : BasicUserInfo ): Long // sqlite return id

    @Delete
    suspend fun deleteUser( localUser : BasicUserInfo): Int // sqlite return id

    @Query("SELECT * FROM basic_user_info where localId == $LOCAL_ID")
    suspend fun getUser(): BasicUserInfo


}