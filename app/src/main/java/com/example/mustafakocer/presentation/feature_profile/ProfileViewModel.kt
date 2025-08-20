package com.example.mustafakocer.presentation.feature_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.R
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.usecase.GetUserProfileUseCase
import com.example.mustafakocer.domain.usecase.UpdateUserProfileUseCase
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _error = MutableStateFlow<com.example.mustafakocer.domain.exception.AppException?>(null)
    val error = _error.asStateFlow()

    private val _toastMessage = MutableSharedFlow<UiText>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            getUserProfileUseCase(forceRefresh = true).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _error.value = resource.exception
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _error.value = null
                        _user.value = resource.data
                    }
                    is Resource.Idle -> { /* No-op */ }
                }
            }
        }
    }

    fun updateProfile(firstName: String, lastName: String, email: String, phone: String, age: String) {
        val currentUser = _user.value ?: return

        val updatedUser = currentUser.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            age = age.toIntOrNull() ?: currentUser.age
        )

        // Only proceed if there are actual changes.
        if (updatedUser == currentUser) {
            viewModelScope.launch { _toastMessage.emit(UiText.StringResource(R.string.toast_no_changes_made)) }
            return
        }

        viewModelScope.launch {
            updateUserProfileUseCase(updatedUser).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isUpdating.value = true
                    is Resource.Error -> {
                        _isUpdating.value = false
                        val errorMessage = resource.exception.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringResource(R.string.toast_update_failed)
                        _toastMessage.emit(errorMessage)
                    }
                    is Resource.Success -> {
                        _isUpdating.value = false
                        _toastMessage.emit(UiText.StringResource(R.string.toast_profile_updated))
                    }
                    is Resource.Idle -> { /* No-op */ }
                }
            }
        }
    }

    /**
     * Called by the Fragment after it has handled an error, to prevent the same
     * error from being processed again on configuration change.
     */
    fun errorHandled() {
        _error.value = null
    }
}