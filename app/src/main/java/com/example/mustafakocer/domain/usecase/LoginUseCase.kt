package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.AuthSession
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(username: String, password: String): Flow<Resource<AuthSession>> {
        // Girdi (input) doğrulaması
        if (username.isBlank() || password.isBlank()) {
            return flow {
                // DÜZELTME: Yeni ve doğru hata tipini kullanıyoruz.
                val error = AppException.Data.InputError("Kullanıcı adı veya şifre boş olamaz.")
                emit(Resource.Error(error))
            }
        }

        // Girdiler geçerliyse, Repository'ye git.
        return authRepository.login(username, password)
    }
}