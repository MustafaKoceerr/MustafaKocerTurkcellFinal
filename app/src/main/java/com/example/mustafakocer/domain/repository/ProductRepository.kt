package com.example.mustafakocer.domain.repository

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Ürünler ve kategorilerle ilgili tüm veri operasyonları için sözleşme (arayüz).
 * Bu arayüz, veri kaynağının (network, veritabanı vb.) detaylarını soyutlar.
 *
 * MİMARİ NOT: Fonksiyonlar artık suspend değil, Flow<Resource<...>> döndürüyor.
 * Bu, UI katmanının veri akışını (Yükleniyor, Başarılı, Hata) reaktif olarak
 * dinlemesini sağlar.
 */
interface ProductRepository {

    /**
     * Tüm ürünlerin listesini ağdan getirir.
     */
    fun getPaginatedProducts(): Flow<PagingData<Product>>

    /**
     * Mevcut tüm ürün kategorilerinin listesini ağdan getirir.
     */
    fun getCategories(): Flow<Resource<List<Category>>>

    /**
     * Belirli bir kategoriye ait ürünlerin listesini ağdan getirir.
     */
    fun getPaginatedProductsByCategory(categoryName: String): Flow<PagingData<Product>> // YENİ

    /**
     * Arama sorgusuyla eşleşen ürünlerin listesini ağdan getirir.
     */
    fun searchPaginatedProducts(query: String): Flow<PagingData<Product>>

    /**
     * Verilen ID listesine sahip ürünlerin detaylarını getirir.
     * Bu, offline-first çalışır; önce veritabanından dener, sonra ağdan çeker.
     */
    fun getProductsByIds(ids: List<Int>): Flow<Resource<List<Product>>>
}