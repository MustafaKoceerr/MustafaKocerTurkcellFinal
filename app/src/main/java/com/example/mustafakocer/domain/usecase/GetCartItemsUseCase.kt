package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userId: String): Flow<Resource<List<CartItem>>> {
        // 1. Firebase'den ham sepet verisini (productId, quantity) dinle.
        val rawCartFlow = cartRepository.getRawCartItems(userId)

        // 2. Ham sepet verisi her değiştiğinde, içindeki product ID'leri al.
        val productIdsFlow = rawCartFlow.map { resource ->
            when (resource) {
                is Resource.Success -> resource.data.map { it.first } // Sadece ID'leri al
                else -> emptyList()
            }
        }

        // 3. Bu ID'lere sahip ürünlerin detaylarını veritabanından dinle.
        val productsFlow = productIdsFlow.flatMapLatest { ids ->
            if (ids.isNotEmpty()) {
                productRepository.getProductsByIds(ids)
            } else {
                // Sepet boşsa, boş bir ürün listesi akışı başlat.
                flowOf(Resource.Success(emptyList()))
            }
        }

        // 4. İki ana akışı (ham sepet ve ürün detayları) birleştir (combine).
        return combine(rawCartFlow, productsFlow) { cartResource, productsResource ->
            // İki akıştan biri bile hazır değilse (Loading/Error), o durumu yansıt.
            if (cartResource !is Resource.Success || productsResource !is Resource.Success) {
                return@combine Resource.Loading // veya gelen hatayı döndür
            }

            val rawCartItems = cartResource.data
            val products = productsResource.data
            val productsMap =
                products.associateBy { it.id } // Ürünleri ID'ye göre map'le (daha hızlı erişim için)

            // Ham sepet verisi ile ürün detaylarını birleştirerek son `CartItem` listesini oluştur.
            val finalCartItems = rawCartItems.mapNotNull { (productId, quantity) ->
                productsMap[productId]?.let { product ->
                    CartItem(
                        product = product,
                        quantity = quantity
                    )
                }
            }
            Resource.Success(finalCartItems)
        }
    }
}