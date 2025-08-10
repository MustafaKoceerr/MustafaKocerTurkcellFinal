package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.data.model.dto.LoginResponseDto
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(username: String, password: String): Flow<Resource<LoginResponseDto>> {
        // 1. İŞ KURALI: Girdi (input) doğrulaması burada yapılır.
        if (username.isBlank() || password.isBlank()) {
            // Ağ isteği atmadan, anında bir hata durumu içeren bir Flow döndür.
            return flow {
                emit(Resource.Error(AppException.Data.ValidationError("Kullanıcı adı veya şifre boş olamaz.")))
            }
        }

        // 2. Girdiler geçerliyse, Repository'ye git.
        return authRepository.login(username, password)
    }
}