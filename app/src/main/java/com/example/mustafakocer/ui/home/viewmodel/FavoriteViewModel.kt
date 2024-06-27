package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: DatabaseRepository
):ViewModel() {

    private val _likedproducts = MutableStateFlow<List<LikedProduct>>(emptyList())
    val likedproducts: StateFlow<List<LikedProduct>> = _likedproducts.asStateFlow()

    fun getAllLikedProducts(userId: Int) {
        viewModelScope.launch(Dispatchers.IO){
            // Make the API call
            val response = repository.gelAllLikedProductsDB(userId)
            // assign the value, it can be success or failure
            _likedproducts.value = response
        }
    }
}