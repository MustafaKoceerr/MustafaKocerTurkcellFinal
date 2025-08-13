package com.example.mustafakocer.data.repository

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CartRepositoryImpl @Inject constructor(
    private val dbRef: DatabaseReference,
) : CartRepository {

    companion object {
        private const val PATH_CARTS = "carts"
    }

    override fun getRawCartItems(userId: String): Flow<Resource<List<Pair<Int, Int>>>> =
        callbackFlow {
            // 1. Dinlenecek doğru Firebase yolunu belirle.
            val cartRef = dbRef.child(PATH_CARTS).child(userId)

            // 2. Dinleyiciye ilk bağlandığında Yükleniyor durumunu gönder.
            trySend(Resource.Loading)

            // 3. Firebase'in ValueEventListener'ını oluştur. Bu, veri her değiştiğinde tetiklenir.
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Gelen veriyi List<Pair<Int, Int>> formatına dönüştür.
                    val rawItems = snapshot.children.mapNotNull { productSnapshot ->
                        try {
                            // key: "114", value: 5L (Long)
                            val productId = productSnapshot.key?.toInt()
                            val quantity = (productSnapshot.value as? Long)?.toInt()

                            // Sadece geçerli ve düzgün formatlanmış veriyi al.
                            if (productId != null && quantity != null) {
                                Pair(productId, quantity)
                            } else {
                                null // Hatalı veriyi (örn: key'i Int olmayan) atla.
                            }
                        } catch (e: Exception) {
                            // Parse etme sırasında bir hata olursa bu satırı atla.
                            null
                        }
                    }
                    // 4. Başarıyla parse edilen listeyi Flow'a gönder.
                    trySend(Resource.Success(rawItems))
                }

                override fun onCancelled(error: DatabaseError) {
                    // 5. Dinleyici iptal edilirse veya bir izin hatası olursa, Hata durumunu gönder.
                    val exception = AppException.Firebase(error.message, error.toException())
                    trySend(Resource.Error(exception))
                    close(exception) // Flow'u hatayla sonlandır.
                }
            }

            // 6. Dinleyiciyi Firebase referansına bağla.
            cartRef.addValueEventListener(listener)

            // 7. Bu Flow dinlenmeyi bıraktığında (coroutine iptal olduğunda),
            // memory leak olmaması için listener'ı mutlaka kaldır.
            awaitClose { cartRef.removeEventListener(listener) }
        }

    override suspend fun addOrIncreaseCartItem(userId: String, productId: Int): Resource<Unit> {
        return try {
            val itemRef = dbRef.child(PATH_CARTS).child(userId).child(productId.toString())
            suspendCancellableCoroutine { continuation ->
                continuation.invokeOnCancellation { /* No-op */ }
                itemRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        // DEĞİŞTİ: Artık CartItemEntity değil, direkt Long (miktar) okuyoruz.
                        val currentQuantity = currentData.getValue(Long::class.java) ?: 0L
                        currentData.value = currentQuantity + 1
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?,
                    ) {
                        if (continuation.isActive) {
                            if (error == null) continuation.resume(Unit)
                            else continuation.resumeWithException(error.toException())
                        }
                    }
                })
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Unknown(e))
        }
    }

    override suspend fun decreaseOrRemoveCartItem(userId: String, productId: Int): Resource<Unit> {
        return try {
            val itemRef = dbRef.child(PATH_CARTS).child(userId).child(productId.toString())
            suspendCancellableCoroutine { continuation ->
                continuation.invokeOnCancellation { /* No-op */ }
                itemRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentQuantity = currentData.getValue(Long::class.java)
                        // Miktar 1 ise veya hiç yoksa, sil (null ata).
                        if (currentQuantity == null || currentQuantity <= 1) {
                            currentData.value = null
                        } else {
                            // Değilse, 1 azalt.
                            currentData.value = currentQuantity - 1
                        }
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?,
                    ) {
                        if (continuation.isActive) {
                            if (error == null) continuation.resume(Unit)
                            else continuation.resumeWithException(error.toException())
                        }
                    }
                })
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Unknown(e))
        }
    }

    override suspend fun clearCart(userId: String): Resource<Unit> {
        return try {
            dbRef.child(PATH_CARTS).child(userId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Unknown(e))
        }
    }

    // YENİ FONKSİYONUN UYGULAMASI
    override suspend fun removeCartItem(userId: String, productId: Int): Resource<Unit> {
        return try {
            // Firebase'de "carts -> {userId} -> {productId}" yolundaki veriyi sil.
            dbRef.child(PATH_CARTS).child(userId).child(productId.toString()).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Ağ hatası veya başka bir Firebase hatası durumunda sarmala.
            Resource.Error(AppException.Unknown(e))
        }
    }
}