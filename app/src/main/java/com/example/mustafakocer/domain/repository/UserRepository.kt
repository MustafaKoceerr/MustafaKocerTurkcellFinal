package com.example.mustafakocer.domain.repository

import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(forceRefresh: Boolean = true): Flow<Resource<User>>

    fun updateUserProfile(user: User): Flow<Resource<User>>

    suspend fun clearLocalUser()
}