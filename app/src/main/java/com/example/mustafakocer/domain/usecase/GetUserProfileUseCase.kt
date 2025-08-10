package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Oturum açmış olan mevcut kullanıcının profil bilgilerini getirme iş kuralı.
 *
 * MİMARİ NOT: Bu UseCase, "Single Source of Truth" (Room DB) prensibini benimseyen
 * UserRepository'den veri akışını alır. ViewModel, verinin nereden geldiği
 * (önce DB, sonra API'den tazeleme) gibi detayları bilmez, sadece bu UseCase'i
 * çağırır ve sonucu reaktif olarak dinler.
 */
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * @param forceRefresh `true` olarak ayarlanırsa, veritabanındaki veriyi
     *                     gösterirken aynı zamanda ağdan yeni veri çekmeyi zorlar.
     * @return Kullanıcı verisini içeren bir Resource akışı.
     */
    operator fun invoke(forceRefresh: Boolean = true): Flow<Resource<User>> {
        return userRepository.getUserProfile(forceRefresh)
    }
}