package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Encapsulates the business logic for retrieving the current user's ID.
 */
class GetUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Executes the use case.
     * @return A [Flow] emitting the user's ID. It may emit null if the user is not
     * logged in or if the ID has not been persisted.
     */
    operator fun invoke(): Flow<Int?> =
        authRepository.getUserId()
}