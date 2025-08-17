package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.util.FirebaseConstants
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
    private val sessionManager: SessionManager,
) : CartRepository {

    // Helper function to get the current user's cart reference.
    // Throws an exception if the user is not logged in.
    private fun getUserCartRef(): DatabaseReference {
        val userId = sessionManager.userId.value
            ?: throw AppException.Session.MissingSessionData("User is not logged in to access the cart.")
        return dbRef.child(FirebaseConstants.PATH_CARTS).child(userId.toString())
    }

    override fun getRawCartItems(): Flow<Resource<List<Pair<Int, Int>>>> = callbackFlow {
        try {
            val cartRef = getUserCartRef()
            send(Resource.Loading)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rawItems = snapshot.children.mapNotNull { productSnapshot ->
                        try {
                            val productId = productSnapshot.key?.toInt()
                            val quantity = (productSnapshot.value as? Long)?.toInt()
                            if (productId != null && quantity != null) Pair(
                                productId,
                                quantity
                            ) else null
                        } catch (e: Exception) {
                            null // Skip malformed data
                        }
                    }
                    trySend(Resource.Success(rawItems))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = AppException.Firebase(error.message, error.toException())
                    trySend(Resource.Error(exception))
                    close(exception)
                }
            }
            cartRef.addValueEventListener(listener)
            awaitClose { cartRef.removeEventListener(listener) }

        } catch (e: AppException) {
            send(Resource.Error(e))
            close(e)
        }
    }

    override suspend fun addOrIncreaseCartItem(productId: Int): Resource<Unit> {
        return try {
            val itemRef = getUserCartRef().child(productId.toString())
            suspendCancellableCoroutine<Unit> { continuation ->
                itemRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentQuantity = currentData.getValue(Long::class.java) ?: 0L
                        currentData.value = currentQuantity + 1
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        data: DataSnapshot?,
                    ) {
                        if (continuation.isActive) {
                            if (error == null) {
                                continuation.resume(Unit) // Başarılı, coroutine'i devam ettir.
                            } else {
                                continuation.resumeWithException(error.toException()) // Hata, coroutine'i exception ile devam ettir.
                            }
                        }
                    }
                })
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Firebase("Failed to update item", e))
        }
    }

    override suspend fun decreaseOrRemoveCartItem(productId: Int): Resource<Unit> {
        return try {
            val itemRef = getUserCartRef().child(productId.toString())
            // DÜZELTME: Callback'i coroutine'e çevirmek için suspendCancellableCoroutine kullanıyoruz.
            suspendCancellableCoroutine<Unit> { continuation ->
                itemRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentQuantity = currentData.getValue(Long::class.java)
                        if (currentQuantity == null || currentQuantity <= 1) {
                            currentData.value = null
                        } else {
                            currentData.value = currentQuantity - 1
                        }
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        data: DataSnapshot?,
                    ) {
                        if (continuation.isActive) {
                            if (error == null) {
                                continuation.resume(Unit) // Başarılı
                            } else {
                                continuation.resumeWithException(error.toException()) // Hata
                            }
                        }
                    }
                })
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Firebase("Failed to update item", e))
        }
    }

    override suspend fun clearCart(): Resource<Unit> {
        return try {
            getUserCartRef().removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Firebase("Failed to clear cart", e))
        }
    }

    override suspend fun removeCartItem(productId: Int): Resource<Unit> {
        return try {
            getUserCartRef().child(productId.toString()).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(AppException.Firebase("Failed to remove item", e))
        }
    }
}