package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.model.User
import com.example.mustafakocer.data.repository.BaseRepository
import com.example.mustafakocer.data.repository.NetworkRepository
import com.example.mustafakocer.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: NetworkRepository
):ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading)
    val user: StateFlow<Resource<User>> = _user.asStateFlow()

    fun getUser(userId: Int) {
        viewModelScope.launch {
            _user.value = Resource.Loading
            // Make the API call
            val response = repository.getUserRepo(userId)
            // assign the value, it can be success or failure
            _user.value = response
        }
    }
}