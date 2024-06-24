package com.example.mustafakocer.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: NetworkRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<Any>>(Resource.Loading)
    val loginState: StateFlow<Resource<Any>> = _loginState.asStateFlow()
    // todo any'leri değiştir
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            // Simulate login action or make API call
            //val result = userRepository.login(username, password)
            //_loginState.value = result
        }
    }
}
