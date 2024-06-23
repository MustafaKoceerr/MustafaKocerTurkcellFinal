package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.Categories
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.model.User
import com.example.mustafakocer.data.network.IDummyApi
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val api: IDummyApi
) : BaseRepository() {

    suspend fun getProductsRepo(): Resource<Products> {
        return safeApiCall {
            api.getProducts()
        }
    }

    suspend fun getCategoriesRepo(): Resource<Categories> {
        return safeApiCall {
            api.getCategories()
        }

    }


    suspend fun getProductsByCategoryRepo(categoryName: String): Resource<Products> {
        return safeApiCall {
            api.getProductsByCategory(categoryName)
        }
    }


    suspend fun getUserRepo(userId: Int): Resource<User> {
        return safeApiCall {
            api.getUserById(userId)
        }
    }


    suspend fun searchProductsRepo(query:String): Resource<Products>{
        return safeApiCall {
            api.searchProducts(query)
        }
    }
}