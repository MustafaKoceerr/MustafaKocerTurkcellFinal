package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    operator fun invoke(userId: String): Flow<Resource<List<CartItem>>> {
        return cartRepository.getRawCartItems(userId).flatMapLatest { cartResource ->
            when (cartResource) {
                is Resource.Loading -> kotlinx.coroutines.flow.flowOf(Resource.Loading)
                is Resource.Error -> kotlinx.coroutines.flow.flowOf(Resource.Error(cartResource.exception))
                is Resource.Idle -> kotlinx.coroutines.flow.flowOf(Resource.Idle)
                is Resource.Success -> {
                    val rawItems = cartResource.data
                    if (rawItems.isEmpty()) {
                        kotlinx.coroutines.flow.flowOf(Resource.Success(emptyList()))
                    } else {
                        val productIds = rawItems.map { it.first }
                        productRepository.getProductsByIds(productIds).map { productsResource ->
                            when (productsResource) {
                                is Resource.Success -> {
                                    val productsMap = productsResource.data.associateBy { it.id }
                                    val finalCartItems = rawItems.mapNotNull { (productId, quantity) ->
                                        productsMap[productId]?.let { product ->
                                            CartItem(product = product, quantity = quantity)
                                        }
                                    }
                                    Resource.Success(finalCartItems)
                                }
                                is Resource.Error -> Resource.Error(productsResource.exception)
                                is Resource.Loading -> Resource.Loading
                                is Resource.Idle -> Resource.Idle
                            }
                        }
                    }
                }
            }
        }
    }
}