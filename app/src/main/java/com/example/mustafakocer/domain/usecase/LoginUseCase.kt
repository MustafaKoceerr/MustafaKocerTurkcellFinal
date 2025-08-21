package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.AuthSession
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Encapsulates the business logic for user login.
 * This includes validating the inputs before delegating the call to the repository.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * Executes the login use case.
     * @param username The user's username.
     * @param password The user's password.
     * @return A [Flow] of [Resource] indicating the login attempt's outcome. It will
     * emit an [AppException.Data.InputError] immediately if inputs are invalid.
     */
    operator fun invoke(username: String, password: String): Flow<Resource<AuthSession>> {
        if (username.isBlank() || password.isBlank()) {
            val error = AppException.Data.InputError("Username and password cannot be empty.")
            return flowOf(Resource.Error(error))
        }

        return authRepository.login(username, password)
    }
}