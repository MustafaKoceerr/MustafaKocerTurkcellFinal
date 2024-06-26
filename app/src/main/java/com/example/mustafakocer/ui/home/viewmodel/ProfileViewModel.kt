package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.model.User
import com.example.mustafakocer.data.repository.DatabaseRepository
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
    ):ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Waiting)
    val user: StateFlow<Resource<User>> = _user.asStateFlow()

    fun getUser(token: String) {
        viewModelScope.launch {
            _user.value = Resource.Loading
            // Make the API call
            val response = repository.getLoggedUserRepo(token)
            // assign the value, it can be success or failure
            _user.value = response
        }
    }

    fun getAuthToken(): Flow<String?> {
        return databaseRepository.collectPreferencesRepo(PreferenceKeys.KEY_AUTH)
    }
}