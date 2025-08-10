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
        Log.d("ProfileDebug", "Repository.updateUserProfile çağrıldı.")

        // DataStore'dan token ve userId'yi çekmeye çalışıyoruz.
        val token = authRepository.getAuthToken().first()
        val userId = authRepository.getUserId().first()

        // Token ve userId'nin durumunu loglayalım.
        Log.d("ProfileDebug", "Alınan Token: $token, Alınan UserID: $userId")

        if (token.isNullOrBlank() || userId == null) {
            Log.e("ProfileDebug", "HATA: Token veya UserID null! API isteği atılamayacak.")
            emit(Resource.Error(AppException.Api.Unauthorized(null)))
            return@flow // Fonksiyondan çık
        }

        Log.d("ProfileDebug", "Token ve UserID geçerli. safeApiCall başlatılıyor...")


        safeApiCall {
            // DEĞİŞTİ: API'ye artık Map yerine DTO'yu gönderiyoruz.
            api.updateUser("Bearer $token", userId, userUpdateDto)
        }.collect { resource ->
            // API'den gelen sonucu loglayalım.
            Log.d("ProfileDebug", "safeApiCall sonucu geldi: $resource")

            when (resource) {
                is Resource.Loading -> emit(Resource.Loading)
                is Resource.Error -> emit(Resource.Error(resource.exception))
                is Resource.Success -> {
                    val updatedUserDto = resource.data
                    // Başarılı yanıttan sonra veritabanını güncelle.
                    userDao.insertOrReplace(updatedUserDto.toEntity())
                    emit(Resource.Success(updatedUserDto.toDomain()))
                }

                is Resource.Idle -> { /* No-op */
                }
            }
        }
    }

    override suspend fun clearLocalUser() {
        userDao.clearUser()
    }
}