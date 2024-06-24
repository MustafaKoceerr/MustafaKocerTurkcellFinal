package com.example.mustafakocer.ui.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.model.LoginResponse
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.repository.DataStoreRepository
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
   // var _loginState by mutableStateOf<Resource<LoginResponse>>(Resource.Loading)
    //        private set
    //val loginState: State<Resource<LoginResponse>>


    private val _loginState = MutableStateFlow<Resource<LoginResponse>>(Resource.Waiting)
    val loginState: StateFlow<Resource<LoginResponse>> = _loginState.asStateFlow()

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            // Simulate login action or make API call
            val result = userRepository.userLoginRepo(username, password)
            _loginState.value = result

        }
    }


    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> get() = _authToken

    private fun collectPreferences(preferenceKey: PreferenceKeys) {
        viewModelScope.launch {
            dataStoreRepository.collectPreferencesRepo(preferenceKey).collect{

            }

        }
    }

}
