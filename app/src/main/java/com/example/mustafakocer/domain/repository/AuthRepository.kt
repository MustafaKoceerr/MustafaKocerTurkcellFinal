package com.example.mustafakocer.domain.repository

import com.example.mustafakocer.domain.model.AuthSession
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    /**
     * Attempts to log in with the given credentials and persists the session upon success.
     * @return A flow emitting the resource state of the login operation.
     * On success, it contains the AuthSession.
     */
    fun login(username: String, password: String): Flow<Resource<AuthSession>>

    /**
     * Retrieves the saved authentication token as a flow.
     */
    fun getAuthToken(): Flow<String?>

    /**
     * Retrieves the saved user ID as a flow.
     */
    fun getUserId(): Flow<Int?>

    /**
     * Clears the persisted session data (token, id).
     */
    suspend fun logout()
}