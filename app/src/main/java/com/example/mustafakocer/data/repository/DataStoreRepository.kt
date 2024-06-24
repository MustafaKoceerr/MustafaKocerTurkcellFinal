package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val preferences: UserPreferences
) {


    suspend fun saveAuthTokenRepo(token: String) {
        preferences.saveAuthToken(token)
    }

    suspend fun saveUserIdRepo(id: String) {
        preferences.saveUserId(id)
    }

    fun collectPreferencesRepo(preferenceKey: PreferenceKeys): Flow<String?>{
        return preferences.getPreferenceFlow(preferenceKey)
    }



    /*
    // view modelde olması gereken kod
       lifecycleScope.launch {
            viewModel.authToken.collect { authToken ->
                // authToken değiştiğinde burada işlemleri yapabilirsiniz
                println("Auth Token: $authToken")
            }
     */
}