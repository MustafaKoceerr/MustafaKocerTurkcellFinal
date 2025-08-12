package com.example.mustafakocer.presentation.feature_product_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.usecase.GetCategoriesUseCase
import com.example.mustafakocer.domain.usecase.GetProductsByCategoryUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
) : ViewModel() {
    // --- Kategorileri Listeleme State'i (Bu değişmedi) ---
    private val _categoriesState = MutableStateFlow<Resource<List<Category>>>(Resource.Idle)
    val categoriesState : StateFlow<Resource<List<Category>>> = _categoriesState.asStateFlow()

    // --- Kategoriye Göre Ürünleri Listeleme State'i (Bu DEĞİŞTİ) ---
    // 1. Seçilen kategori adını tutacak bir StateFlow.
    private val _selectedCategory = MutableStateFlow<String?>(null)

    // 2. Kategori adı değiştikçe, yeni PagingData akışını tetikleyecek olan ana Flow.
    @OptIn(ExperimentalCoroutinesApi::class)
    val productsByCategoryFlow: Flow<PagingData<Product>> = _selectedCategory
        .flatMapLatest { categoryName->
            // Eğer kategori adı null veya boş değilse, UseCase'i çağır.
            // Değilse, boş bir PagingData akışı döndür.
            if (!categoryName.isNullOrBlank()){
                getProductsByCategoryUseCase(categoryName)
            }else{
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)
    // PagingData'yı ViewModelScope'ta önbelleğe alarak konfigürasyon
    // değişikliklerinde verinin korunmasını sağlıyoruz.

    init {
        // ViewModel oluşturulduğunda, kategori listesini otomatik olarak çek.
        fetchCategories()
    }

    /**
     * Tüm kategorilerin listesini getirmek için UseCase'i tetikler.
     */
    fun fetchCategories() {
        getCategoriesUseCase().onEach { resource ->
            _categoriesState.value = resource
        }.launchIn(viewModelScope)
    }

    /**
     * Fragment'tan gelen kategori adını güncelleyerek ürün akışını tetikler.
     * Bu fonksiyon, ProductsByCategoryFragment tarafından çağrılacak.
     *
     * @param categoryName Ürünleri getirilecek olan kategorinin 'slug' adı.
     */
    fun onCategorySelected(categoryName: String) {
        _selectedCategory.value = categoryName
    }

}