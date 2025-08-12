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
    private val dbRef: DatabaseReference
) : CartRepository {

    companion object {
        private const val PATH_CARTS = "carts"
    }

    override fun getRawCartItems(userId: String): Flow<Resource<List<Pair<Int, Int>>>> = callbackFlow {
        val cartRef = dbRef.child(PATH_CARTS).child(userId)
        trySend(Resource.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // DEĞİŞTİ: Artık daha basit bir veri yapısını parse ediyoruz.
                val rawItems = snapshot.children.mapNotNull { productSnapshot ->
                    try {
                        val productId = productSnapshot.key?.toInt()
                        val quantity = (productSnapshot.value as? Long)?.toInt()
                        if (productId != null && quantity != null) {
                            Pair(productId, quantity)
                        } else null
                    } catch (e: Exception) {
                        null // Hatalı veriyi (örn: key'i Int olmayan) atla
                    }
                }
                trySend(Resource.Success(rawItems))
            }

            override fun onCancelled(error: DatabaseError) {
                val exception = AppException.Unknown(error.toException())
                trySend(Resource.Error(exception))
                close(exception)
            }
        }
        cartRef.addValueEventListener(listener)
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

                    override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
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

                    override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
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
}