package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.CartItemBasic
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

/**
 * Implements the [CartRepository] interface, providing a bridge to the Firebase Realtime Database.
 * It handles all cart-related operations, translating Firebase's callback-based API into
 * modern coroutine Flows and suspend functions.
 */
class CartRepositoryImpl @Inject constructor(
    private val dbRef: DatabaseReference,
    private val sessionManager: SessionManager,
) : CartRepository {

    /**
     * A centralized helper to get the current user's cart reference.
     * Throws a specific session exception if the user is not logged in.
     */
    private fun getUserCartRef(): DatabaseReference {
        val userId = sessionManager.userId.value
            ?: throw AppException.Session.MissingSessionData("User is not logged in to access the cart.")
        return dbRef.child(FirebaseConstants.PATH_CARTS).child(userId.toString())
    }

    /**
     * A private wrapper to centralize error handling for all suspend Firebase operations.
     * It mirrors the `safeApiCall` pattern for consistency.
     */
    private suspend fun <T> safeFirebaseCall(block: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(block())
        } catch (e: Exception) {
            Resource.Error(AppException.Unknown(e))
        }
    }

    /**
     * A suspend function that wraps the verbose Firebase Transaction API in a coroutine.
     */
    private suspend fun runTransactionSuspend(
        itemRef: DatabaseReference,
        operation: (currentQuantity: Long) -> Long?,
    ) {
        suspendCancellableCoroutine<Unit> { continuation ->
            itemRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentQuantity = currentData.getValue(Long::class.java) ?: 0L
                    currentData.value = operation(currentQuantity)
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    data: DataSnapshot?,
                ) {
                    if (continuation.isActive) {
                        if (error == null) continuation.resume(Unit)
                        else continuation.resumeWithException(error.toException())
                    }
                }
            })
        }
    }


    override fun getRawCartItems(): Flow<Resource<List<CartItemBasic>>> = callbackFlow {
        try {
            val cartRef = getUserCartRef()
            trySend(Resource.Loading)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rawItems = snapshot.children.mapNotNull { productSnapshot ->
                        productSnapshot.key?.toIntOrNull()?.let { productId ->
                            (productSnapshot.value as? Long)?.toInt()?.let { quantity ->
                                CartItemBasic(productId = productId, quantity = quantity)
                            }
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

        } catch (e: AppException) {
            trySend(Resource.Error(e))
            close(e)
        }
    }

    override suspend fun addOrIncreaseCartItem(productId: Int): Resource<Unit> = safeFirebaseCall {
        val itemRef = getUserCartRef().child(productId.toString())
        runTransactionSuspend(itemRef) { currentQuantity -> currentQuantity + 1 }
    }

    override suspend fun decreaseOrRemoveCartItem(productId: Int): Resource<Unit> =
        safeFirebaseCall {
            val itemRef = getUserCartRef().child(productId.toString())
            runTransactionSuspend(itemRef) { currentQuantity ->
                if (currentQuantity <= 1) null else currentQuantity - 1
            }
        }

    override suspend fun clearCart(): Resource<Unit> = safeFirebaseCall {
        getUserCartRef().removeValue().await()
    }

    override suspend fun removeCartItem(productId: Int): Resource<Unit> = safeFirebaseCall {
        getUserCartRef().child(productId.toString()).removeValue().await()
    }
}