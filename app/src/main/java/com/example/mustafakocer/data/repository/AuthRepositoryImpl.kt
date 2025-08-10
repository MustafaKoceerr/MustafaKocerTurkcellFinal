package com.example.mustafakocer.data.repository


import com.example.mustafakocer.data.model.dto.LoginRequestDto
import com.example.mustafakocer.data.model.dto.LoginResponseDto
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.preferences.SessionStorage
import com.example.mustafakocer.data.preferences.UserPreferences
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val userPreferences: UserPreferences,
    private val sessionStorage: SessionStorage,
) : AuthRepository {

    /**
     * Sadece API'ye giriş isteği atar ve sonucu DTO olarak döndürür.
     * Herhangi bir mapping veya kaydetme işlemi yapmaz.
     */
    override fun login(username: String, password: String): Flow<Resource<LoginResponseDto>> {
        return safeApiCall {
            api.login(LoginRequestDto(username = username, password = password))
        }
    }

    /**
     * Gelen token ve userId'yi ilgili güvenli depolama alanlarına kaydeder.
     */
    override suspend fun saveSession(token: String, userId: Int) {
        sessionStorage.saveAuthToken(token)
        userPreferences.saveUserId(userId)
    }

    /**
     * Şifrelenmiş depolamadan token akışını döndürür.
     */
    override fun getAuthToken(): Flow<String?> {
        return sessionStorage.authTokenFlow
    }

    /**
     * DataStore'dan userId akışını döndürür.
     */
    override fun getUserId(): Flow<Int?> {
        return userPreferences.userId
    }

    /**
     * Sadece oturumla ilgili depolama alanlarını temizler.
     * Kullanıcı profili veritabanına dokunmaz.
     */
    override suspend fun logout() {
        sessionStorage.clear()
        userPreferences.clear()
    }
}