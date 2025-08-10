package com.example.mustafakocer.presentation.feature_profile

import androidx.lifecycle.ViewModel
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.usecase.GetUserProfileUseCase
import com.example.mustafakocer.domain.usecase.UpdateUserProfileUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.dto.UserUpdateDto
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {

    // 1. Kullanıcı verisini tutan StateFlow
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    // 2. Sayfanın ilk yüklenme durumunu tutan StateFlow
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // 3. "Güncelle" butonuna basıldığındaki yüklenme durumunu tutan StateFlow
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    // 4. Toast mesajı gibi tek seferlik olaylar için SharedFlow
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        // ViewModel oluşturulduğunda, mevcut kullanıcı profilini çek.
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        // Bu fonksiyon, veritabanını dinleyen ve gerektiğinde ağı tetikleyen
        // `networkBoundResource`'u kullandığı için, `user` akışını sürekli güncel tutar.
        getUserProfileUseCase(forceRefresh = true).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isLoading.value = true
                is Resource.Error -> {
                    _isLoading.value = false
                    _toastMessage.emit(
                        resource.exception.message ?: "Kullanıcı bilgileri alınamadı."
                    )
                }

                is Resource.Success -> {
                    _isLoading.value = false
                    _user.value = resource.data
                }

                is Resource.Idle -> { /* No-op */
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Fragment'tan çağrılacak olan public güncelleme fonksiyonu.
     * UI'dan gelen yeni değerleri parametre olarak alır.
     */
    fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        age: String,
    ) {
        val currentUser = _user.value ?: return

        // DEĞİŞTİ: Map oluşturmak yerine, DTO'yu oluşturuyoruz.
        val userUpdateDto = UserUpdateDto(
            firstName = if (firstName != currentUser.firstName) firstName else null,
            lastName = if (lastName != currentUser.lastName) lastName else null,
            email = if (email != currentUser.email) email else null,
            phone = if (phone != currentUser.phone) phone else null,
            age = if (age != currentUser.age.toString()) age.toIntOrNull() else null
        )

        // Eğer hiçbir alan değiştirilmediyse, DTO'nun tüm alanları null olacaktır.
        // Bunu kontrol etmenin daha temiz bir yolu.
        if (userUpdateDto.firstName == null && userUpdateDto.lastName == null &&
            userUpdateDto.email == null && userUpdateDto.phone == null && userUpdateDto.age == null) {
            viewModelScope.launch { _toastMessage.emit("Değişiklik yapılmadı.") }
            return
        }
        updateUserProfileUseCase(userUpdateDto).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isUpdating.value = true
                is Resource.Error -> {
                    _isUpdating.value = false
                    _toastMessage.emit(resource.exception.message ?: "Güncelleme başarısız.")
                }

                is Resource.Success -> {
                    _isUpdating.value = false
                    // Başarılı olunca veritabanı zaten güncellendi. `fetchUserProfile`'ın dinlediği
                    // Flow otomatik olarak tetiklenip `_user` StateFlow'unu güncelleyecektir.
                    _toastMessage.emit("Profil başarıyla güncellendi!")
                }

                is Resource.Idle -> { /* No-op */
                }
            }
        }.launchIn(viewModelScope)
    }
}

