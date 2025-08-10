package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.entity.CartItemEntity
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
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
        // Bu fonksiyonun yapısı doğru ve güncel. Değişiklik yok.
        val cartRef = dbRef.child(PATH_CARTS).child(userId)
        trySend(Resource.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rawItems = snapshot.children.mapNotNull { data ->
                    val entity = data.getValue(CartItemEntity::class.java)
                    if (entity?.productId != null && entity.quantity != null) {
                        Pair(entity.productId, entity.quantity)
                    } else { null }
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
            // YENİ VE DOĞRU YAPI: Coroutines 1.10.2+ için
            suspendCancellableCoroutine { continuation ->
                // Coroutine iptal edilirse ne olacağını baştan tanımlıyoruz.
                continuation.invokeOnCancellation { /* Firebase transaction'ı için özel bir iptal işlemi yok */ }

                itemRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentItem = currentData.getValue(CartItemEntity::class.java)
                        if (currentItem == null) {
                            currentData.value = CartItemEntity(productId = productId, quantity = 1)
                        } else {
                            val newQuantity = (currentItem.quantity ?: 0) + 1
                            currentData.value = currentItem.copy(quantity = newQuantity)
                        }
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                        if (continuation.isActive) {
                            if (error == null) {
                                // DEĞİŞTİ: Artık sadece tek parametreli resume() kullanılıyor.
                                continuation.resume(Unit)
                            } else {
                                continuation.resumeWithException(error.toException())
                            }
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
            // YENİ VE DOĞRU YAPI: Coroutines 1.10.2+ için
            suspendCancellableCoroutine { continuation ->
                continuation.invokeOnCancellation { /* No-op */ }

                itemRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentItem = currentData.getValue(CartItemEntity::class.java)
                        if (currentItem != null) {
                            val newQuantity = (currentItem.quantity ?: 0) - 1
                            if (newQuantity <= 0) {
                                currentData.value = null
                            } else {
                                currentData.value = currentItem.copy(quantity = newQuantity)
                            }
                        }
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                        if (continuation.isActive) {
                            if (error == null) {
                                // DEĞİŞTİ: Artık sadece tek parametreli resume() kullanılıyor.
                                continuation.resume(Unit)
                            } else {
                                continuation.resumeWithException(error.toException())
                            }
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
        // Bu fonksiyon zaten doğruydu, değişiklik yok.
        return try {
            dbRef.child(PATH_CARTS).child(userId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Unknown(e))
        }
    }
}