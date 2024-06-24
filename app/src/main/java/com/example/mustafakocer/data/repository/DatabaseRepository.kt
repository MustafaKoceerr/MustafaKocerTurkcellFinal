package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.UserPreferences
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.db.entity.BasicUserInfo
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val db: AppDatabase,
    private val preferences: UserPreferences

) {

    suspend fun insertProductRepo(likedProduct: LikedProduct): Long {
        return db.createProductDao().insertProduct(likedProduct)
    }



    suspend fun gelAllLikedProductsDB(userId: Int): List<LikedProduct> {
        return db.createProductDao().getAllProducts(userId)
    }


    suspend fun insertUser(localUser: BasicUserInfo): Long {
        return db.createUserDao().insertUser(localUser)
    }

    suspend fun deleteUser(localUser: BasicUserInfo): Int {
        return db.createUserDao().deleteUser(localUser)
    }

    suspend fun getUser(): BasicUserInfo {
        return db.createUserDao().getUser()
    }




    //######################DataStoreOperations######################
    suspend fun saveAuthTokenRepo(token: String) {
        preferences.saveAuthToken(token)
    }

    suspend fun saveUserIdRepo(id: String) {
        preferences.saveUserId(id)
    }

    fun collectPreferencesRepo(preferenceKey: PreferenceKeys): Flow<String?> {
        return preferences.getPreferenceFlow(preferenceKey)
    }

    suspend fun clearDataStoreRepo() {
        preferences.clearDataStore()
    }



}