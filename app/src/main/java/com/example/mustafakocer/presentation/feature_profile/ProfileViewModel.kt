package com.example.mustafakocer.presentation.feature_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // 4. Toast mesajı gibi tek seferlik olaylar için SharedFlow
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
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

        // DÜZELTME 1: Artık DTO değil, tam bir User domain nesnesi oluşturuyoruz.
        val updatedUser = currentUser.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            age = age.toIntOrNull() ?: currentUser.age
        )

        // DÜZELTME 2: Gereksiz DTO oluşturma ve null kontrol mantığı kaldırıldı.
        // Bunun yerine, data class'ların yapısal eşitlik kontrolünü kullanıyoruz.
        if (updatedUser == currentUser) {
            viewModelScope.launch { _toastMessage.emit("Değişiklik yapılmadı.") }
            return
        }

        // DÜZELTME 3: UseCase'e artık doğru tip olan `updatedUser` nesnesini gönderiyoruz.
        updateUserProfileUseCase(updatedUser).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isUpdating.value = true
                is Resource.Error -> {
                    _isUpdating.value = false
                    _toastMessage.emit(resource.exception.message ?: "Güncelleme başarısız.")
                }

                is Resource.Success -> {
                    _isUpdating.value = false
                    // `getUserProfileUseCase`'in dinlediği Flow, veritabanındaki
                    // değişiklikten sonra `_user` StateFlow'unu otomatik olarak güncelleyecektir.
                    _toastMessage.emit("Profil başarıyla güncellendi!")
                }

                is Resource.Idle -> { /* No-op */
                }
            }
        }.launchIn(viewModelScope)
    }
}