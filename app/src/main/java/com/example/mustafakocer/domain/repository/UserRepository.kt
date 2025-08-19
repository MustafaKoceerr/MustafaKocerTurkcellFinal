package com.example.mustafakocer.domain.repository

import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * A contract for the data layer to handle all user profile data operations,
 * typically following a Single Source of Truth (SSOT) pattern.
 */
interface UserRepository {
    /**
     * Retrieves the user's profile.
     * The implementation should provide data from a local cache first, and then
     * potentially update it from the network.
     *
     * @param forceRefresh If true, a network fetch should be triggered regardless of the
     * cache state. If false, the network should only be used if the cache is empty.
     * @return A flow emitting the resource state of the user profile.
     */
    fun getUserProfile(forceRefresh: Boolean = true): Flow<Resource<User>>

    /**
     * Updates the user's profile data.
     * The implementation should send the update to the network and, on success,
     * refresh the local cache.
     *
     * @param user The [User] object with the updated information.
     * @return A flow emitting the resource state of the update operation.
     */
    fun updateUserProfile(user: User): Flow<Resource<User>>

    /**
     * Clears any locally cached user data. This is typically called on logout.
     */
    suspend fun clearLocalUser()
}