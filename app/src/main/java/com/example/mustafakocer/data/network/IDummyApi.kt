package com.example.mustafakocer.data.network

import com.example.mustafakocer.data.db.entity.CartRequest
import com.example.mustafakocer.data.model.CartResponse
import com.example.mustafakocer.data.model.Categories
import com.example.mustafakocer.data.model.LoginResponse
import com.example.mustafakocer.data.model.OrderResponse
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IDummyApi {

    companion object {
        // invoke -> MoviesApi() yazınca çalışacak fonksiyondur, özel bir keydir
        private val BASE_URL = "https://dummyjson.com/"

        operator fun invoke(): IDummyApi {

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(IDummyApi::class.java)
        }
        // singleton yapısı sağlıyor
    }

    @GET("products")
    suspend fun getProducts(): Products


    @GET("products/categories")
    suspend fun getCategories(): Categories

    @GET("products/category/{category_name}")
    suspend fun getProductsByCategory(@Path("category_name") categoryName: String): Products


    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): Products


    @Headers("Content-Type: application/json")
    @POST("carts/add")
    suspend fun cartInfo(@Body cartRequest: CartRequest): CartResponse


    @FormUrlEncoded
    @POST("auth/login")
    suspend fun userLogin(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse


    @GET("carts/user/{id}")
    suspend fun getCartsByUser(@Path("id") id: String): OrderResponse


    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: User
    ): User

}