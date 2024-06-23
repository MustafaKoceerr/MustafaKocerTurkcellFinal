package com.example.mustafakocer.data.network

import com.example.mustafakocer.data.model.Categories
import com.example.mustafakocer.data.model.Products
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ICategoryApi {

    /*
     @GET("{category_name}")
     suspend fun getCategoriesOrProducts(@Path("category_name") categoryName: String? = null) : Category
     */
    @GET("products/categories")
    suspend fun getCategories(): Categories

    @GET("products/category/{category_name}")
    suspend fun getProductsByCategory(@Path("category_name") categoryName: String): Products

    companion object {
        // invoke -> MoviesApi() yazınca çalışacak fonksiyondur, özel bir keydir
        private val BASE_URL = "https://dummyjson.com/products/"


        operator fun invoke(): ICategoryApi {

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(ICategoryApi::class.java)
        }
        // singleton yapısı sağlıyor
    }

}