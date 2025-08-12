package com.example.mustafakocer.data.repository

import android.util.Log
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.mapper.toEntity
import com.example.mustafakocer.data.model.dto.UserUpdateDto
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.util.networkBoundResource
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val db: AppDatabase,
    private val authRepository: AuthRepository,
) : UserRepository {

    private val userDao = db.createUserDao()

    override fun getUserProfile(forceRefresh: Boolean): Flow<Resource<User>> {
        return networkBoundResource(
            query = {
                userDao.getUser().map { entity ->
                    // Veritabanında kullanıcı yoksa (ilk açılış anı), geçici bir User nesnesi döndür.
                    entity?.toDomain() ?: User(
                        id = 0,
                        firstName = "",
                        lastName = "",
                        email = "",
                        phone = "",
                        username = "Yükleniyor...",
                        age = 0,
                        imageUrl = ""
                    )
                }
            },
            fetch = {
                val token = authRepository.getAuthToken().first()
                if (token.isNullOrBlank()) {
                    throw IllegalStateException("Token not found for fetching user profile.")
                }
                api.getCurrentUser("Bearer $token")
            },
            saveFetchResult = { response ->
                response.body()?.let { userDetailDto ->
                    userDao.insertOrReplace(userDetailDto.toEntity())
                }
            },
            shouldFetch = { user ->
                // forceRefresh true ise VEYA mevcut veri geçici ise ağı tetikle.
                forceRefresh || user.id == 0
            }
        )
    }


    override fun updateUserProfile(userUpdateDto: UserUpdateDto): Flow<Resource<User>> = flow {
        // 1. Oturum bilgilerini al. .first() suspend olduğu için bu flow builder içinde olmalı.
        val token = authRepository.getAuthToken().first()
        val userId = authRepository.getUserId().first()

        // 2. Oturum kontrolü yap.
        if (token.isNullOrBlank() || userId == null) {
            emit(Resource.Error(AppException.Api.Unauthorized(null)))
            return@flow // Akışı sonlandır.
        }

        // 3. Güvenli API çağrısını yap ve sonucu işle.
        safeApiCall {
            api.updateUser("Bearer $token", userId, userUpdateDto)
        }.collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val updatedUserDto = resource.data
                    // Önce veritabanını (Single Source of Truth) güncelle.
                    userDao.insertOrReplace(updatedUserDto.toEntity())
                    // Sonra başarılı sonucu Domain modeliyle emit et.
                    emit(Resource.Success(updatedUserDto.toDomain()))
                }
                // Hata veya Yüklenme durumlarını doğrudan emit et.
                is Resource.Error -> emit(resource)
                is Resource.Loading -> emit(resource)
                is Resource.Idle -> emit(resource)
            }
        }
    }

    override suspend fun clearLocalUser() {
        userDao.clearUser()
    }
}