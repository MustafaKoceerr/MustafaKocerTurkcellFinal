package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * DataStore'dan mevcut kullanıcının ID'sini getirme iş kuralını kapsüller.
 */
class GetUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * @return Kullanıcı ID'sini içeren bir Flow. Eğer kullanıcı giriş yapmamışsa
     *         veya ID kaydedilmemişse null dönebilir.
     */
    operator fun invoke(): Flow<Int?> {
        return authRepository.getUserId()
    }
}