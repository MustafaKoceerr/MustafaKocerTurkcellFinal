package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Kullanıcı oturumunu tamamen sonlandırma iş kuralı.
 * Oturumla ilgili tüm kalıcı verileri (token, id, profil, sepet) temizler.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository // Sepeti temizlemek için eklendi
) {
    suspend operator fun invoke() {
        // DÜZELTME: Önce silinecek sepetin sahibini bul.
        // .first() ile Flow'dan o anki değeri alıyoruz. Bu suspend bir işlemdir.
        val userIdToClear = authRepository.getUserId().first()

        // Oturum anahtarlarını (token, id) DataStore ve EncryptedPrefs'ten sil.
        authRepository.logout()

        // Yerel veritabanındaki kullanıcı profilini sil.
        userRepository.clearLocalUser()

        // DÜZELTME: Eğer bir kullanıcı ID'si varsa, o kullanıcının sepetini Firebase'den sil.
        userIdToClear?.let { userId ->
            cartRepository.clearCart(userId.toString())
        }
    }
}