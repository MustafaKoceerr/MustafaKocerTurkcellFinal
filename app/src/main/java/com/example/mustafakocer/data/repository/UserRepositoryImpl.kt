package com.example.mustafakocer.data.repository

import android.util.Log
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.db.UserDao
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.mapper.toEntity
import com.example.mustafakocer.data.mapper.toUpdateDto
import com.example.mustafakocer.data.model.dto.UserDetailDto
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.data.util.networkBoundResource
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val userDao: UserDao,
    private val sessionManager: SessionManager,
) : UserRepository {

    override fun getUserProfile(forceRefresh: Boolean): Flow<Resource<User>> = channelFlow {
        send(Resource.Loading)

        val isInitiallyDbEmpty = userDao.getUser().first() == null

        launch {
            subscribeToDatabaseChanges()
        }

        if (forceRefresh || isInitiallyDbEmpty) {
            fetchFromNetworkAndSave(isInitiallyDbEmpty)
        }
    }

    private suspend fun ProducerScope<Resource<User>>.subscribeToDatabaseChanges() {
        userDao.getUser().collect { entity ->
            entity?.let { send(Resource.Success(it.toDomain())) }
        }
    }

    private suspend fun ProducerScope<Resource<User>>.fetchFromNetworkAndSave(wasDbEmpty: Boolean) {
        try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                userDao.insertOrReplace(response.body()!!.toEntity())
            } else if (wasDbEmpty) {
                send(
                    Resource.Error(
                        AppException.Api.HttpError(
                            response.code(),
                            response.message(),
                            null
                        )
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Network error fetching profile", e)
            if (wasDbEmpty) {
                send(Resource.Error(AppException.Network.NoInternet(e)))
            }
        }
    }

    override fun updateUserProfile(user: User): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        val userId = sessionManager.userId.value

        if (userId == null) {
            val error =
                AppException.Session.MissingSessionData("User ID not found for update operation.")
            emit(Resource.Error(error))
            return@flow
        }

        safeApiCall {
            val userUpdateDto = user.toUpdateDto()
            api.updateUser(userId, userUpdateDto)
        }.collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val updatedUserDto = resource.data
                    userDao.insertOrReplace(updatedUserDto.toEntity())
                    emit(Resource.Success(updatedUserDto.toDomain()))
                }

                is Resource.Error -> emit(resource)
                else -> Unit
            }
        }
    }

    override suspend fun clearLocalUser() {
        userDao.clearUser()
    }
}