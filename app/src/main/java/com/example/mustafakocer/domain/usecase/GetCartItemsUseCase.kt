package com.example.mustafakocer.domain.usecase

import android.util.Log
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val errorMapper: ErrorMapper,
) {
    operator fun invoke(): Flow<Resource<List<CartItem>>> {
        return cartRepository.getRawCartItems().map { rawResource ->
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
                rawItems.map { (productId, quantity) ->
                    async {
                        try {
                            // DEĞİŞTİ: Artık basit bir suspend fonksiyonu çağırıyoruz.
                            val product = productRepository.getProduct(productId)
                            CartItem(product = product, quantity = quantity)
                        } catch (e: Exception) {
                            // Eğer tek bir ürün alınamazsa (silinmiş olabilir),
                            // bunu loglayıp sepet listesinden atlıyoruz.
                            Log.w(
                                "GetCartItemsUseCase",
                                "Product with ID $productId could not be fetched for cart.",
                                e
                            )
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
            Resource.Success(cartItems)
        } catch (e: Exception) {
            // Bu, coroutineScope'un kendisinde bir sorun olursa diye bir güvenlik ağıdır.
            Log.e("GetCartItemsUseCase", "An unexpected error occurred during product fetching.", e)
            Resource.Error(errorMapper.map(e))
        }
    }
}