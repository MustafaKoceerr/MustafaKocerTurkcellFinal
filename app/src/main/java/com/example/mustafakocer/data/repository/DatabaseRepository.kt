package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.UserPreferences
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.db.entity.BasicUserInfo
import com.example.mustafakocer.data.db.entity.Cart
import com.example.mustafakocer.data.db.entity.LikedProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val db: AppDatabase,
    private val preferences: UserPreferences

) {

    // // ################ DB Product Operations ################
    suspend fun insertProductDBRepo(likedProduct: LikedProduct): Long {
        return db.createProductDao().insertProduct(likedProduct)
    }

    suspend fun deleteProductDBRepo(userId:Int, productId:Int): Int {
        return db.createProductDao().deleteLikedProduct(userId,productId)
    }

    suspend fun gelAllLikedProductsDB(userId: Int): List<LikedProduct> {
        return db.createProductDao().getAllProducts(userId)
    }

    suspend fun getOneProductDB(userId: Int, productId: Int): LikedProduct? {
        return db.createProductDao().getOneProduct(userId, productId)
    }


    // ################ DB User Operations ################
    suspend fun insertUser(localUser: BasicUserInfo): Long {
        return db.createUserDao().insertUser(localUser)
    }

    suspend fun deleteUser(): Int {
        return db.createUserDao().deleteUserById()
    }

    suspend fun getUser(): BasicUserInfo {
        return db.createUserDao().getUser()
    }


    // ################ DataStoreOperations ################

     suspend fun saveAuthTokenRepo(token: String) {
         preferences.saveAuthToken(token)
     }
    fun collectPreferencesRepo(preferenceKey: PreferenceKeys): Flow<String?> {
        return preferences.getPreferenceFlow(preferenceKey)
    }

    suspend fun clearDataStoreRepo() {
        preferences.clearDataStore()
    }


    // ################ DB Cart Operations ################

    suspend fun insertCartDBRepo(cart: Cart): Long {
        return db.createProductDao().insertCart(cart)
    }

    suspend fun deleteCartDBRepo(userId:Int, productId:Int): Int {
        return db.createProductDao().deleteCart(userId,productId)
    }

    suspend fun gelAllCartsDBRepo(userId: Int): List<Cart> {
        return db.createProductDao().getAllCarts(userId)
    }

    suspend fun getOneCartDBRepo(userId: Int, productId: Int): Cart? {
        return db.createProductDao().getOneCart(userId, productId)
    }

    suspend fun updateCartDBRepo(cart: Cart): Int {
        return db.createProductDao().updateCart(cart)
    }
}