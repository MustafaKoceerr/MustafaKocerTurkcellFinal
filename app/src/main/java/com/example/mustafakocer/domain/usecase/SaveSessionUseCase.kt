package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Kullanıcı oturum anahtarlarını (token ve ID) kalıcı depolamaya kaydetme iş kuralı.
 */
class SaveSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String, userId: Int) {
        authRepository.saveSession(token, userId)
    }
}