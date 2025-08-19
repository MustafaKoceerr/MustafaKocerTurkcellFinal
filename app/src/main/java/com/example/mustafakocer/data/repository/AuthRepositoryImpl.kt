package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.dto.LoginRequestDto
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.data.preferences.toAuthSession
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.model.AuthSession
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.domain.util.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val sessionManager: SessionManager, // Artık tek bağımlılık bu
    private val errorMapper: ErrorMapper // Enjekte edildi
) : AuthRepository {

    override fun login(username: String, password: String): Flow<Resource<AuthSession>> {
        return safeApiCall(errorMapper) {
            api.login(LoginRequestDto(username = username, password = password))
        }.onEach { resource ->
            // If the login is successful, update the session via the SessionManager.
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
        // Delegate directly to the SessionManager's in-memory flow.
        return sessionManager.authToken
    }

    override fun getUserId(): Flow<Int?> {
        // Delegate directly to the SessionManager's in-memory flow.
        return sessionManager.userId
    }

    override suspend fun logout() {
        // Delegate the entire logout logic to the SessionManager.
        sessionManager.clearSession()
    }
}