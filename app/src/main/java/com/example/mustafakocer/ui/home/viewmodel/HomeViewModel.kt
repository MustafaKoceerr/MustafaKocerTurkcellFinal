package com.example.mustafakocer.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.repository.DatabaseRepository
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
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

    fun insertProduct(likedProduct: LikedProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            val insertedProductId = databaseRepository.insertProductRepo(likedProduct)
            if (insertedProductId != -1L) {
                Log.d("ProductViewModel", "Product inserted successfully with id: $insertedProductId")
            } else {
                Log.e("ProductViewModel", "Failed to insert product")
                // Burada bir hata durumu ele alınabilir
            }
        }
    }

    fun getUserId(): Flow<String?> {
        return databaseRepository.collectPreferencesRepo(PreferenceKeys.USER_ID)
    }



    private val _favoriteProducts = MutableStateFlow<List<LikedProduct>>(emptyList())
    val favoriteProducts: StateFlow<List<LikedProduct>> = _favoriteProducts

    fun gelAllLikedProductsDB(userId: Int) {
        viewModelScope.launch {
            try {
                val products = databaseRepository.gelAllLikedProductsDB(userId)
                _favoriteProducts.value = products
            } catch (e: Exception) {
                // Handle error scenario, e.g., log or show error message
                Log.e("ProductViewModel", "Error fetching favorite products", e)
            }
        }
    }


}