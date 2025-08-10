package com.example.mustafakocer.domain.repository

import com.example.mustafakocer.data.model.dto.LoginResponseDto
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow




interface AuthRepository {

    /**
     * Verilen kullanıcı adı ve şifre ile giriş yapmayı dener.
     * @return Giriş sonucunu, ham 'LoginResponseDto' ile birlikte bir Resource akışı
     *         olarak döndürür. Token ve ID'yi kaydetme sorumluluğu ViewModel/UseCase'dedir.
     */
    fun login(username: String, password: String): Flow<Resource<LoginResponseDto>>

    /**
     * Verilen oturum anahtarlarını (token ve userId) kalıcı depolamaya kaydeder.
     */
    suspend fun saveSession(token: String, userId: Int)

    /**
     * Kaydedilmiş yetkilendirme token'ını bir akış olarak döndürür.
     */
    fun getAuthToken(): Flow<String?>

    /**
     * Kaydedilmiş kullanıcı ID'sini bir akış olarak döndürür.
     */
    fun getUserId(): Flow<Int?>

    /**
     * Sadece oturum anahtarlarını (token, id) kalıcı depolamadan temizler.
     */
    suspend fun logout()
}