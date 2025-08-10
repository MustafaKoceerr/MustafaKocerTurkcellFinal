package com.example.mustafakocer.presentation.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.usecase.GetCartItemsUseCase
import com.example.mustafakocer.domain.usecase.GetUserIdUseCase
import com.example.mustafakocer.domain.usecase.GetUserProfileUseCase
import com.example.mustafakocer.domain.usecase.LogoutUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserIdUseCase: GetUserIdUseCase, // YENİ BAĞIMLILIK
    private val getCartItemsUseCase: GetCartItemsUseCase, // YENİ BAĞIMLILIK
) : ViewModel() {
    // Ürün getirme gibi diğer özelliklere ait sorumluluklar buradan tamamen kaldırıldı.

    // Mevcut kullanıcının durumunu tutan StateFlow.
    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val userState: StateFlow<Resource<User>> = _userState.asStateFlow()

    // Çıkış yapma gibi tek seferlik olayları UI'a bildirmek için.
    private val _logoutEvent = Channel<Unit>()
    val logoutEvent: Flow<Unit> = _logoutEvent.receiveAsFlow()

    // YENİ: Sepetteki toplam ürün sayısını tutacak StateFlow
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    init {
        // ViewModel oluşturulduğunda kullanıcı profilini çekmeye başla.
        fetchUserProfile()
        observeCartCount() // YENİ: Sepeti dinlemeye başla
    }

    private fun fetchUserProfile() {
        getUserProfileUseCase().onEach { resource ->
            _userState.value = resource
        }.launchIn(viewModelScope)
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            // Önce kullanıcı ID'sini al
            val userId = getUserIdUseCase().first()?.toString()
            if (userId != null) {
                // Kullanıcı ID'si varsa, sepeti dinlemeye başla
                getCartItemsUseCase(userId).onEach { resource ->
                    if (resource is Resource.Success) {
                        // Başarılı bir şekilde sepet verisi geldiğinde,
                        // listedeki tüm ürünlerin quantity'lerini topla.
                        val totalCount = resource.data.sumOf { it.quantity }
                        _cartItemCount.value = totalCount
                    }
                }.launchIn(viewModelScope)
            }
        }
    }


    /**
     * UI'dan (örn: NavigationView menüsü) "Çıkış Yap" eylemi geldiğinde
     * bu fonksiyon tetiklenir.
     */
    fun onLogoutClicked() {
        viewModelScope.launch {
            // Önce yüklenme durumunu gösterebiliriz (opsiyonel).
            // ...

            // Çıkış yapma iş kuralını çalıştır.
            logoutUseCase()

            // İşlem bittiğinde, Activity'ye "artık AuthActivity'ye gidebilirsin"
            // sinyalini gönder.
            _logoutEvent.send(Unit)
        }
    }
}