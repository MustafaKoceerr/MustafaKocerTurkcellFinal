package com.example.mustafakocer.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.db.entity.BasicUserInfo
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.repository.DatabaseRepository
import com.example.mustafakocer.data.repository.NetworkRepository
import com.example.mustafakocer.util.UserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {


    private val _products = MutableStateFlow<Resource<Products>>(Resource.Waiting)
    val products: StateFlow<Resource<Products>> = _products.asStateFlow()
    fun getProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading
            // Make the API call
            val response = repository.getProductsRepo()
            // assign the value, it can be success or failure
            _products.value = response
        }
    }

    fun getAuthToken(): Flow<String?> {
        return databaseRepository.collectPreferencesRepo(PreferenceKeys.KEY_AUTH)
    }


    private val _userInfo = MutableStateFlow<BasicUserInfo?>(null)
    val userInfo: StateFlow<BasicUserInfo?> = _userInfo.asStateFlow()
    fun getUser() {
        viewModelScope.launch {
            // Make the API call
            val result = databaseRepository.getUser()
            // assign the value, it can be success or failure
            Log.d("problem", "get user result $result")

            _userInfo.value = result
        }
    }


    suspend fun deleteUser(): Int {
        val result = databaseRepository.deleteUser()
        return result
    }

    // NOT BU İŞLEMLER YAPILMADAN LOGİN ACTİVİTY'YE GEÇMESİNİ İSTEMEDİĞİM İÇİN
    // BUNLARA SUSPEND VERDİM VE MAİN'DE ÇAĞIRACAĞIM
    suspend fun clearDataStore() {
        databaseRepository.clearDataStoreRepo()
    }


}