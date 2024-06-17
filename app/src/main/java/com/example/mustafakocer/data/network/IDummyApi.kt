package com.example.mustafakocer.data.network

import com.example.mustafakocer.data.model.Products
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

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
    suspend fun getProducts() : Response<Products>


}