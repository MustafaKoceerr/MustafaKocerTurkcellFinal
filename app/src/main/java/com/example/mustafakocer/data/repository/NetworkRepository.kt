package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.entity.CartRequest
import com.example.mustafakocer.data.model.dto.CartResponse
import com.example.mustafakocer.data.model.Categories
import com.example.mustafakocer.data.model.dto.OrderResponse
import com.example.mustafakocer.data.model.dto.Products
import com.example.mustafakocer.data.model.dto.User
import com.example.mustafakocer.data.network.IDummyApi
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val api: IDummyApi,
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


    suspend fun searchProductsRepo(query: String): Resource<Products> {
        return safeApiCall {
            api.searchProducts(query)
        }
    }


    suspend fun cartInfoRepo(cartRequest: CartRequest): Resource<CartResponse> {
        return safeApiCall {
            api.cartInfo(cartRequest)
        }
    }


    suspend fun userLoginRepo(username: String, password:String): Resource<LoginResponse> {
        return safeApiCall {
            api.userLogin(username,password)
        }
    }

    suspend fun getCartsByUserRepo(userId: String): Resource<OrderResponse> {
        return safeApiCall {
            api.getCartsByUser(userId)
        }
    }

    suspend fun updateUser(userId:Int, user :User): Resource<User> {
        return safeApiCall {
            api.updateUser(userId,user)
        }
    }

}