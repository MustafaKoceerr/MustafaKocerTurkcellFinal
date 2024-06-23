package com.example.mustafakocer.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface IDummyHeaderApi {

    companion object {
        // invoke -> MoviesApi() yazınca çalışacak fonksiyondur, özel bir keydir
        private val BASE_URL = "https://dummyjson.com/"

        operator fun invoke(authToken:String?=null): IDummyHeaderApi {

            return Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor{ chain ->
                            // we will use this chain to add our header to the request
                            chain.proceed(chain.request().newBuilder().also {
                                it.addHeader("Authorization","Bearer $authToken")
                            }.build())
                        }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(IDummyHeaderApi::class.java)
        }
        // singleton yapısı sağlıyor
    }


}