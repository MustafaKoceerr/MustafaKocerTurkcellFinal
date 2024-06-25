package com.example.mustafakocer.ui.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.db.entity.BasicUserInfo
import com.example.mustafakocer.data.model.LoginResponse
import com.example.mustafakocer.data.model.Resource
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
class LoginViewModel @Inject constructor(
    private val userRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
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


    /*
      fun saveAuthToken(token: String) {
          viewModelScope.launch {
              dataStoreRepository.saveAuthTokenRepo(token)
          }
      }

      fun saveUserId(id: String) {
          viewModelScope.launch {
              dataStoreRepository.saveUserIdRepo(id)
          }
      }
     */
    suspend fun saveAuthToken(token: String) {
        databaseRepository.saveAuthTokenRepo(token)
    }
        // bunları kaydedip activity değiştireceğim için ve bunlar kaydedilirkenb, activity'nin değişme
    // ihtimali olduğu için bunları suspend yapıp UI'da çağırıcam böylelikle işlemler sırayla gerçekleşecek
        fun getAuthToken(): Flow<String?> {
            return databaseRepository.collectPreferencesRepo(PreferenceKeys.KEY_AUTH)
        }

/*
    suspend fun saveUserId(id: String) {
        databaseRepository.saveUserIdRepo(id)
    }

    fun getUserId(): Flow<String?> {
        return databaseRepository.collectPreferencesRepo(PreferenceKeys.USER_ID)
    }
 */


    fun clearDataStore() {
        viewModelScope.launch {
            databaseRepository.clearDataStoreRepo()
        }
    }

    suspend fun saveUser(basicUserInfo: BasicUserInfo):Long{
        val result = databaseRepository.insertUser(basicUserInfo)
        return result
    }

    suspend fun getUser(): BasicUserInfo {
        val result = databaseRepository.getUser()
        return result
    }

}
