package com.example.mustafakocer.data.network

import com.example.mustafakocer.data.model.dto.CategoriesResponseDto
import com.example.mustafakocer.data.model.dto.ProductsResponseDto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IDummyApi {

    // Bu endpoint artık ProductsResponseDto bekliyor.
    @GET("products")
    suspend fun getProducts(): ProductsResponseDto

    // Bu endpoint CategoriesResponseDto (yani List<CategoryDto>) bekliyor.
    @GET("products/categories")
    suspend fun getCategories(): CategoriesResponseDto

    // Bu endpoint de ProductsResponseDto bekliyor.
    @GET("products/category/{category_name}")
    suspend fun getProductsByCategory(@Path("category_name") categoryName: String): ProductsResponseDto

    // Bu endpoint de ProductsResponseDto bekliyor.
    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ProductsResponseDto

    // Diğer endpoint'ler (login, cart vb.) şimdilik aynı kalabilir.
    // Onları kendi dikey dilimlerinde refaktör edeceğiz.

    companion object {
        private const val BASE_URL = "https://dummyjson.com/"

        // Hilt modülünde daha merkezi bir yerden provide edeceğiz ama
        // şimdilik Gson'u Kotlinx Serialization ile değiştirelim.
        operator fun invoke(): IDummyApi {
            val json = Json { ignoreUnknownKeys = true } // API'den gelen bilmediğimiz alanları görmezden gel.
            val contentType = "application/json".toMediaType()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory(contentType)) // YENİ CONVERTER
                .build()
                .create(IDummyApi::class.java)
        }
    }
}