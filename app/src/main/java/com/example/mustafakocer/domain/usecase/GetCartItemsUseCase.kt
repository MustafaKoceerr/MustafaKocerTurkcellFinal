package com.example.mustafakocer.domain.usecase

import android.util.Log
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.model.CartItemBasic
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A business rule that combines raw cart data (product IDs and quantities) with full
 * product details to create a list of rich [CartItem] models suitable for the UI.
 */
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

    /**
     * Fetches full product details for each raw cart item in parallel and combines them.
     * If a single product fetch fails, it is logged and omitted from the final list.
     */
    private suspend fun fetchAndCombineProducts(
        rawItems: List<CartItemBasic>
    ): Resource<List<CartItem>> {
        if (rawItems.isEmpty()) {
            return Resource.Success(emptyList())
        }
        return try {
            val cartItems = coroutineScope {
                rawItems.map { item ->
                    async {
                        try {
                            val product = productRepository.getProduct(item.productId)
                            CartItem(product = product, quantity = item.quantity)
                        } catch (e: Exception) {
                            // If a single product is unavailable (e.g., deleted from backend),
                            // log the issue and skip it, allowing the rest of the cart to load.
                            Log.w(
                                "GetCartItemsUseCase",
                                "Could not fetch product with ID ${item.productId} for cart. It may have been removed.",
                                e
                            )
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
            Resource.Success(cartItems)
        } catch (e: Exception) {
            // This is a safeguard for unexpected errors within the coroutineScope itself.
            Log.e("GetCartItemsUseCase", "An unexpected error occurred during parallel product fetching.", e)
            Resource.Error(errorMapper.map(e))
        }
    }
}
