package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * The business rule for fetching the profile of the currently logged-in user.
 *
 * ARCHITECTURAL NOTE: This UseCase gets its data stream from a UserRepository that
 * adopts the "Single Source of Truth" (Room DB) principle. The ViewModel is unaware
 * of the data's origin (e.g., DB first, then refreshed from API); it simply
 * invokes this UseCase and reactively listens to the result.
 */
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * Executes the use case.
     * @param forceRefresh If set to `true`, it forces a network fetch to update the
     *                     cached data, even if local data exists.
     * @return A [Flow] of [Resource] containing the user's profile.
     */
    operator fun invoke(forceRefresh: Boolean = true): Flow<Resource<User>> =
        userRepository.getUserProfile(forceRefresh)
}