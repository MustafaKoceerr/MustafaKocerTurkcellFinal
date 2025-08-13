package com.example.mustafakocer.domain.usecase

import android.util.Log
import com.example.mustafakocer.data.network.error.ErrorMapper
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    operator fun invoke(userId: String): Flow<Resource<List<CartItem>>> {
        return cartRepository.getRawCartItems(userId).map { rawResource ->
            when (rawResource) {
                is Resource.Loading -> Resource.Loading
                is Resource.Error -> Resource.Error(rawResource.exception)
                is Resource.Idle -> Resource.Idle
                is Resource.Success -> fetchAndCombineProducts(rawResource.data)
            }
        }
    }

    private suspend fun fetchAndCombineProducts(rawItems: List<Pair<Int, Int>>): Resource<List<CartItem>> {
        if (rawItems.isEmpty()) {
            return Resource.Success(emptyList())
        }
        return try {
            val cartItems = coroutineScope {
                val deferreds = rawItems.map { (productId, quantity) ->
                    async {
                        val productResource = productRepository.getSingleProduct(productId)
                            .firstOrNull { it is Resource.Success } as? Resource.Success<Product>

                        if (productResource != null) {
                            CartItem(product = productResource.data, quantity = quantity)
                        } else {
                            Log.w("GetCartItemsUseCase", "Product with ID $productId could not be fetched.")
                            null
                        }
                    }
                }
                deferreds.awaitAll().filterNotNull()
            }
            Resource.Success(cartItems)
        } catch (e: Exception) {
            Log.e("GetCartItemsUseCase", "An unexpected error occurred during product fetching.", e)
            Resource.Error(ErrorMapper.map(e))
        }
    }
}