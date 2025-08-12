package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Belirtilen ID'ye sahip ürünün detaylarını getirme iş kuralını kapsüller.
 */
class GetProductDetailUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    /**
     * MİMARİ NOT: Bu UseCase, Presentation katmanını (ViewModel) Data katmanının
     * implementasyon detaylarından soyutlar. ViewModel, sadece "bana ürün detayını ver"
     * der, verinin API'den mi, veritabanından mı yoksa önbellekten mi geldiğini bilmez.
     *
     * @param productId Detayları getirilecek ürünün ID'si.
     * @return Ürün detaylarını içeren bir Resource akışı.
     */
    operator fun invoke(productId: Int): Flow<Resource<ProductDetail>> {
        return productRepository.getProductDetail(productId)
    }
}