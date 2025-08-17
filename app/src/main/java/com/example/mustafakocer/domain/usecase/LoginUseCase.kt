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
    // DÖNÜŞ TİPİ DEĞİŞTİ: Artık DTO değil, domain modeli olan AuthSession döndürüyor.
    operator fun invoke(username: String, password: String): Flow<Resource<AuthSession>> {
        if (username.isBlank() || password.isBlank()) {
            return flow {
                emit(Resource.Error(AppException.Data.ValidationError("Kullanıcı adı veya şifre boş olamaz.")))
            }
        }
        return authRepository.login(username, password)
    }
}