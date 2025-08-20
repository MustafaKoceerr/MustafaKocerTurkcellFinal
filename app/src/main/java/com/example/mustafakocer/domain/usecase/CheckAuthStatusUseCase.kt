package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * A business rule to perform a one-time check of the user's authentication status.
 */
class CheckAuthStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * Executes the use case to determine if a user is currently logged in.
     * It performs a simple check to see if a non-empty auth token exists.
     *
     * In a real-world scenario, this could be expanded to check for token expiration.
     *
     * @return `true` if a valid token exists, `false` otherwise.
     */
    suspend operator fun invoke(): Boolean {
        val token = authRepository.getAuthToken().firstOrNull()
        return !token.isNullOrBlank()
    }
}