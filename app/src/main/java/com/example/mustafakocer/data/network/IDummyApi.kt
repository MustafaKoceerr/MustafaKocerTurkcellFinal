package com.example.mustafakocer.data.network

import com.example.mustafakocer.data.model.dto.CategoriesResponseDto
import com.example.mustafakocer.data.model.dto.LoginRequestDto
import com.example.mustafakocer.data.model.dto.LoginResponseDto
import com.example.mustafakocer.data.model.dto.OrdersResponseDto
import com.example.mustafakocer.data.model.dto.ProductsResponseDto
import com.example.mustafakocer.data.model.dto.UserDetailDto
import com.example.mustafakocer.data.model.dto.UserUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IDummyApi {

    // Artık tüm fonksiyonlar Response<T> döndürüyor.
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
    ): Response<ProductsResponseDto>

    @GET("products/categories")
    suspend fun getCategories(): Response<CategoriesResponseDto>

    @GET("products/category/{category_name}")
    suspend fun getProductsByCategory(
        @Path("category_name") categoryName: String,
        @Query("limit") limit: Int, // YENİ
        @Query("skip") skip: Int,     // YENİ
    ): Response<ProductsResponseDto>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
    ): Response<ProductsResponseDto>

    /**
     * Kullanıcı girişi için yetkilendirme isteği gönderir.
     * @param loginRequest Kullanıcı adı ve şifreyi içeren istek gövdesi.
     * @return Giriş başarılı olursa kullanıcı bilgilerini ve token'ı içeren bir yanıt.
     */
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDto,
    ): Response<LoginResponseDto>

    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String,
    ): Response<UserDetailDto> // Daha önce tasarladığımız UserDetailDto'yu kullanıyoruz.

    @GET("carts/user/{userId}")
    suspend fun getOrdersByUserId(
        @Path("userId") userId: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
    ): Response<OrdersResponseDto>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body userUpdateDto: UserUpdateDto
    ): Response<UserDetailDto>

}
