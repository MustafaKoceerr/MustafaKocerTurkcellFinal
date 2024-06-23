package com.example.mustafakocer.data.network

import com.example.mustafakocer.data.model.Categories
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

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
    suspend fun getProducts() : Products

    @GET("users/1")
    suspend fun getUser1() : Products

    @GET("products/categories")
    suspend fun getCategories(): Categories

    @GET("products/category/{category_name}")
    suspend fun getProductsByCategory(@Path("category_name") categoryName: String): Products


    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int) : User


}