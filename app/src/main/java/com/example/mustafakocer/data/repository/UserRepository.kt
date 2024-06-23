package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.model.User
import com.example.mustafakocer.data.network.IUsersApi
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: IUsersApi
):BaseRepository() {


    suspend fun getUserRepo(userId: Int): Resource<User> {
        return safeApiCall {
            api.getUserById(userId)
        }
    }

}