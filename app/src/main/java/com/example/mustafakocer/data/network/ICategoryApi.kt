package com.example.mustafakocer.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ICategoryApi {

    companion object {
        // invoke -> MoviesApi() yazınca çalışacak fonksiyondur, özel bir keydir
        private val BASE_URL = "https://dummyjson.com/category/"

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