package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    /*
    private val _updateUser = MutableStateFlow<Resource<User>>(Resource.Waiting)
    val updateUser: StateFlow<Resource<User>> = _updateUser.asStateFlow()

    fun UpdateUser(userId: Int, user: User) {
        viewModelScope.launch {
            _updateUser.value = Resource.Loading
            // Simulate login action or make API call
            val result = repository.updateUser(userId, user)
            _updateUser.value = Resource.Loading

            _updateUser.value = result

        }
    }
     */
    private val _updateUser = MutableLiveData<Resource<User>>()
    val updateUser: LiveData<Resource<User>> get() = _updateUser

    fun UpdateUser(userId: Int, user: User) {
        viewModelScope.launch {
            _updateUser.value = Resource.Loading

            // Simulate update action or make API call
            val result = repository.updateUser(userId, user)

            _updateUser.value = result
        }
    }


}