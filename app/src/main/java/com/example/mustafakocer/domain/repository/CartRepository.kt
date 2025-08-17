package com.example.mustafakocer.domain.repository

// Artık CartItem'a bağımlı değil.
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    /**
     * Kullanıcının sepetindeki ham veriyi (productId ve quantity) dinler.
     */
    fun getRawCartItems(): Flow<Resource<List<Pair<Int, Int>>>>

    suspend fun addOrIncreaseCartItem(productId: Int): Resource<Unit>
    suspend fun decreaseOrRemoveCartItem(productId: Int): Resource<Unit>
    suspend fun clearCart(): Resource<Unit>
    suspend fun removeCartItem(productId: Int): Resource<Unit>

}