package com.example.mustafakocer.domain.usecase

import android.util.Log
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ...
import com.example.mustafakocer.data.model.dto.UserUpdateDto // YENİ IMPORT

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    // DEĞİŞTİ: Parametre artık Map değil, DTO.
    operator fun invoke(userUpdateDto: UserUpdateDto): Flow<Resource<User>> {
        return userRepository.updateUserProfile(userUpdateDto)
    }
}