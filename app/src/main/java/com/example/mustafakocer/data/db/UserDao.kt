package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mustafakocer.data.model.entity.BasicUserInfo
import com.example.mustafakocer.data.model.entity.LOCAL_ID

@Dao
interface UserDao {

    // Giriş yapan kullanıcıya hoş geldin demek için burada user'ı tutuyorum

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(localUser: BasicUserInfo): Long // sqlite return id

    @Query("DELETE FROM basic_user_info WHERE localId = $LOCAL_ID")
    suspend fun deleteUserById(): Int
    // tek bir tane kullanıcım olduğu için bu şekilde silebiliyorum

    @Query("SELECT * FROM basic_user_info where localId == $LOCAL_ID")
    suspend fun getUser(): BasicUserInfo


}