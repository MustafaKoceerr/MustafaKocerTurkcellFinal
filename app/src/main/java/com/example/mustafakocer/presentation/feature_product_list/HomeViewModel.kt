package com.example.mustafakocer.presentation.feature_product_list

import androidx.lifecycle.ViewModel
import com.example.mustafakocer.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Product
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {

    // Paging3 state yönetimini kendisi yapar.
    val productsFlow: Flow<PagingData<Product>> = getProductsUseCase()
        .cachedIn(viewModelScope)

}



