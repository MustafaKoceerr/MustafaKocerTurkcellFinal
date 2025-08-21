package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Encapsulates the business logic for a complete user logout.
 * This use case orchestrates multiple repositories to ensure all session-related
 * data is cleared from both local storage and remote services where applicable.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    /**
     * Executes the logout process:
     * 1. Clears session tokens from secure storage.
     * 2. Clears the cached user profile from the local database.
     * 3. Clears the user's shopping cart in Firebase.
     */
    suspend operator fun invoke() {
        authRepository.logout()
        userRepository.clearLocalUser()
    }
}