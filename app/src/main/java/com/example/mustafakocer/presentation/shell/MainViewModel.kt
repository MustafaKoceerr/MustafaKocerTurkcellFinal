package com.example.mustafakocer.presentation.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.usecase.GetUserProfileUseCase
import com.example.mustafakocer.domain.usecase.LogoutUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the UI state and logic for the main application shell (MainActivity).
 * It is responsible for providing app-wide data, such as the current user's profile,
 * and handling global actions like logging out.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    /**
     * A state flow that holds the current user's profile information.
     * It uses `stateIn` to convert the cold flow from the use case into a hot,
     * observable StateFlow, which is the recommended modern approach.
     */
    val userState: StateFlow<Resource<User>> = getUserProfileUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )

    private val _logoutEvent = Channel<Unit>()
    /**
     * A one-time event flow to signal that the logout process is complete and the
     * UI should navigate back to the authentication flow.
     */
    val logoutEvent: Flow<Unit> = _logoutEvent.receiveAsFlow()

    /**
     * Handles the user's request to log out by invoking the logout use case and
     * sending a one-time event to trigger navigation.
     */
    fun onLogoutClicked() {
        viewModelScope.launch {
            logoutUseCase()
            _logoutEvent.send(Unit)
        }
    }
}