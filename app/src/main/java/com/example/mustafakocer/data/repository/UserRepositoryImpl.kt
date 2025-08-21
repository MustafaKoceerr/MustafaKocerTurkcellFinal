package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.db.UserDao
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.mapper.toEntity
import com.example.mustafakocer.data.mapper.toUpdateDto
import com.example.mustafakocer.data.network.DummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.domain.util.mapSuccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Implements the [UserRepository] interface, applying a Single Source of Truth (SSOT) pattern.
 * It uses the local database ([UserDao]) as the primary data source and synchronizes it
 * with the network ([DummyApi]).
 */
class UserRepositoryImpl @Inject constructor(
    private val api: DummyApi,
    private val userDao: UserDao,
    private val sessionManager: SessionManager,
    private val errorMapper: ErrorMapper,
) : UserRepository {

    /**
     * Gets the user profile using a SSOT strategy.
     * It immediately emits data from the local cache, then triggers a network refresh if needed.
     * Any network errors are emitted downstream, allowing the UI to inform the user.
     */
    override fun getUserProfile(forceRefresh: Boolean): Flow<Resource<User>> = channelFlow {
        send(Resource.Loading)

        // Subscribe to database changes first to provide cached data immediately.
        val dbSubscription = launch {
            userDao.getUser().collect { entity ->
                entity?.let { send(Resource.Success(it.toDomain())) }
            }
        }

        val isCacheEmpty = userDao.getUser().first() == null
        if (forceRefresh || isCacheEmpty) {
            try {
                // Fetch fresh data from the network.
                val response = api.getCurrentUser()
                if (response.isSuccessful && response.body() != null) {
                    // On success, save to DB. The flow above will automatically emit the update.
                    userDao.insertOrReplace(response.body()!!.toEntity())
                } else {
                    // Always emit an error on failure, so the UI is aware.
                    send(Resource.Error(errorMapper.map(HttpException(response))))
                }
            } catch (e: Exception) {
                // Always emit an error on failure, so the UI is aware.
                send(Resource.Error(errorMapper.map(e)))
            }
        }

        // Clean up the subscription when the flow is cancelled.
        awaitClose { dbSubscription.cancel() }
    }

    /**
     * Updates the user profile on the server and, upon success, updates the local cache.
     */
    override fun updateUserProfile(user: User): Flow<Resource<User>> {
        val userId = sessionManager.userId.value
        if (userId == null) {
            return flowOf(Resource.Error(AppException.Session.MissingSessionData("User ID not found for update.")))
        }

        return safeApiCall(errorMapper) {
            api.updateUser(userId, user.toUpdateDto())
        }.onEach { resource ->
            // Side-effect: If the network call is successful, update the local database.
            if (resource is Resource.Success) {
                userDao.insertOrReplace(resource.data.toEntity())
            }
        }.map { resource ->
            // Transformation: Map the DTO result to a Domain model for the UI.
            resource.mapSuccess { it.toDomain() }
        }
    }

    /**
     * Clears the user data from the local cache, typically on logout.
     */
    override suspend fun clearLocalUser() {
        userDao.clearUser()
    }
}