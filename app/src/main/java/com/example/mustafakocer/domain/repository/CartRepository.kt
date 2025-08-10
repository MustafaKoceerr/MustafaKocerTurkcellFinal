package com.example.mustafakocer.domain.repository

// Artık CartItem'a bağımlı değil.
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    /**
     * Kullanıcının sepetindeki ham veriyi (productId ve quantity) dinler.
     */
    fun getRawCartItems(userId: String): Flow<Resource<List<Pair<Int, Int>>>>

    // ... add, decrease, clear fonksiyonları aynı kalabilir ...
    suspend fun addOrIncreaseCartItem(userId: String, productId: Int): Resource<Unit>
    suspend fun decreaseOrRemoveCartItem(userId: String, productId: Int): Resource<Unit>
    suspend fun clearCart(userId: String): Resource<Unit>
}