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
    // TODO: Sepet UseCase'leri buraya eklenecek
) : ViewModel() {

    // Paging3 state yönetimini kendisi yapar.
    val productsFlow: Flow<PagingData<Product>> = getProductsUseCase()
        .cachedIn(viewModelScope)
    /**
     * MİMARİ NOT: .cachedIn(viewModelScope), Paging 3'ün en önemli operatörlerinden biridir.
     * Bu operatör, PagingData akışını bir CoroutineScope içinde önbelleğe alır.
     * Bu sayede, ekran döndürme gibi konfigürasyon değişikliklerinde,
     * API'den veya veritabanından tekrar veri çekmek yerine, mevcut yüklenmiş
     * veriler ve sayfalama durumu korunur. Bu, hem performansı artırır
     * hem de kullanıcı deneyimini iyileştirir.
     */


    // TODO: Kullanıcı etkileşimlerini (event'leri) yönetecek fonksiyonlar
    // onProductClicked(product: Product)
    // onAddToCartClicked(product: Product)
    // onRemoveFromCartClicked(product: Product)
}
