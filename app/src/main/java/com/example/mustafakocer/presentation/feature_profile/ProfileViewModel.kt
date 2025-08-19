package com.example.mustafakocer.presentation.feature_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.exception.AppException
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
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    // 4. Hata durumunu Fragment'a bildirmek için yeni bir StateFlow
    private val _error = MutableStateFlow<AppException?>(null)
    val error = _error.asStateFlow()

    // 5. Toast mesajı gibi tek seferlik olaylar için SharedFlow
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        getUserProfileUseCase(forceRefresh = true).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _isLoading.value = true
                    _error.value = null // Yükleme başlarken eski hatayı temizle
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    _error.value = resource.exception // Hata durumunu state'e ata
                }
                is Resource.Success -> {
                    _isLoading.value = false
                    _error.value = null // Başarılı durumda hatayı temizle
                    _user.value = resource.data
                }
                is Resource.Idle -> { /* No-op */ }
            }
        }.launchIn(viewModelScope)
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        age: String,
    ) {
        val currentUser = _user.value ?: return

        val updatedUser = currentUser.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            age = age.toIntOrNull() ?: currentUser.age
        )

        if (updatedUser == currentUser) {
            viewModelScope.launch { _toastMessage.emit("Değişiklik yapılmadı.") }
            return
        }

        updateUserProfileUseCase(updatedUser).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isUpdating.value = true
                is Resource.Error -> {
                    _isUpdating.value = false
                    // Güncelleme hatasını Toast olarak göstermeye devam edelim,
                    // çünkü bu tam sayfa bir hata durumu değil.
                    _toastMessage.emit(resource.exception.message ?: "Güncelleme başarısız.")
                }
                is Resource.Success -> {
                    _isUpdating.value = false
                    // Başarılı güncelleme sonrası `_user` state'i zaten
                    // `fetchUserProfile`'ın dinlediği akış tarafından güncellenecek.
                    _toastMessage.emit("Profil başarıyla güncellendi!")
                }
                is Resource.Idle -> { /* No-op */ }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Fragment, hatayı gösterdikten sonra bu fonksiyonu çağırarak
     * hata durumunu temizler. Bu, ekran döndüğünde aynı hatanın
     * tekrar işlenmesini engeller.
     */
    fun errorHandled() {
        _error.value = null
    }
}