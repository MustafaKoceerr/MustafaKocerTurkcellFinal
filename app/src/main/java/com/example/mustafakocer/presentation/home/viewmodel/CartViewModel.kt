package com.example.mustafakocer.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.entity.Cart
import com.example.mustafakocer.data.model.entity.CartRequest
import com.example.mustafakocer.data.model.dto.CartResponse
import com.example.mustafakocer.data.repository.DatabaseRepository
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
):ViewModel() {

    private val _cart = MutableStateFlow<Resource<CartResponse>>(Resource.Waiting)
    val cart: StateFlow<Resource<CartResponse>> = _cart.asStateFlow()

    fun cartInfo(cartRequest: CartRequest) {
        viewModelScope.launch {
            _cart.value = Resource.Loading
            // Make the API call
            val response = repository.cartInfoRepo(cartRequest)
            // assign the value, it can be success or failure
            _cart.value = response
        }
    }

    suspend fun GetAllCarts(userId: Int): List<Cart> {

            val carts = databaseRepository.gelAllCartsDBRepo(userId)
            Log.d("CartViewModel", "Fetched carts: $carts")
            return carts
    }


}