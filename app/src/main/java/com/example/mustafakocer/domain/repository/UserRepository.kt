package com.example.mustafakocer.domain.repository

import com.example.mustafakocer.data.model.dto.UserUpdateDto
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Kullanıcı verileriyle ilgili tüm operasyonlar için sözleşme.
 */
interface UserRepository {

    /**
     * Mevcut kullanıcının profil bilgilerini bir akış olarak döndürür.
     *
     * MİMARİ NOT: Bu fonksiyon, "Single Source of Truth" (Room DB) prensibini uygular.
     * Önce veritabanındaki mevcut veriyi anında yayınlar. Ardından, arka planda
     * API'den veriyi tazeleyip veritabanını günceller. Veritabanındaki değişiklik,
     * Flow tarafından otomatik olarak yakalanır ve UI'a yansıtılır.
     *
     * @param forceRefresh Arka planda API'den veri tazelemesini zorunlu kılmak için.
     * @return Kullanıcı verisini içeren bir Resource akışı.
     */
    fun getUserProfile(forceRefresh: Boolean = true): Flow<Resource<User>>

    /**
     * Yerel veritabanındaki mevcut kullanıcı verilerini temizler.
     * Bu, 'logout' işlemi sırasında kullanılır.
     */
    suspend fun clearLocalUser()


    fun updateUserProfile(userUpdateDto: UserUpdateDto): Flow<Resource<User>>
}