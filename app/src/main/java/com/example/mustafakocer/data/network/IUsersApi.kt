package com.example.mustafakocer.data.network

import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface IUsersApi {

   @GET("{id}")
    suspend fun getUserById(@Path("id") id: Int) : User



    companion object {
        // invoke -> MoviesApi() yazınca çalışacak fonksiyondur, özel bir keydir
        private val BASE_URL = "https://dummyjson.com/users/"

        operator fun invoke(): IUsersApi {

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(IUsersApi::class.java)
        }
        // singleton yapısı sağlıyor
    }

}