package com.example.mustafakocer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mustafakocer.data.db.entity.SelectedProduct
import com.example.mustafakocer.data.db.entity.User
import com.example.mustafakocer.data.model.Product

@Dao
interface UserDao {
    // todo user'ı'da kaydedip, hoş geldin mustafa vb yap

 /*
    @Insert
    fun insertUser(selectedProduct: SelectedProduct): Long // sqlite return id

    @Delete
    fun deleteUser(selectedProduct: SelectedProduct): Long // sqlite return id

    @Query("SELECT * FROM user WHERE localUserId == 1")
    fun getUser(): User?
  */

}