package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * Kullanıcının oturum açıp açmadığını kontrol eden iş kuralı
 */
class CheckAuthStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * @return Token mevcut ve geçerliyse 'true', değilse 'false' döner.
     */
    suspend operator fun invoke(): Boolean {
        // DataStore'dan token'ı bir kerelik oku.
        val token = authRepository.getAuthToken().firstOrNull()
        // Token'ın sadece null veya boş olup olmadığını kontrol et.
        // Gerçek bir uygulamada burada token'ın süresinin dolup dolmadığı
        // gibi daha karmaşık kontroller de yapılabilir.
        return !token.isNullOrBlank()
    }
}