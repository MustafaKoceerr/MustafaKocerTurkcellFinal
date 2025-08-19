package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.dto.LoginRequestDto
import com.example.mustafakocer.data.network.DummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.data.mapper.toAuthSession
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.model.AuthSession
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.domain.util.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Implements the [AuthRepository] interface, acting as the central point for authentication.
 * It orchestrates network calls via [DummyApi], manages session state changes via [SessionManager],
 * and ensures all operations are wrapped with consistent error handling via [ErrorMapper].
 */
class AuthRepositoryImpl @Inject constructor(
    private val api: DummyApi,
    private val sessionManager: SessionManager,
    private val errorMapper: ErrorMapper
) : AuthRepository {

    override fun login(username: String, password: String): Flow<Resource<AuthSession>> {
        return safeApiCall(errorMapper) {
            api.login(LoginRequestDto(username = username, password = password))
        }.onEach { resource ->
            if (resource is Resource.Success) {
                val sessionData = resource.data
                sessionManager.updateSession(token = sessionData.token, id = sessionData.id)
            }
        }.map { resource ->
            resource.mapSuccess { loginResponseDto ->
                loginResponseDto.toAuthSession()
            }
        }
    }

    override fun getAuthToken(): Flow<String?> {
        return sessionManager.authToken
    }

    override fun getUserId(): Flow<Int?> {
        return sessionManager.userId
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}