package com.example.mustafakocer.presentation.feature_details

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mustafakocer.databinding.FragmentProductDetailBinding // DEĞİŞTİ
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment :
    BaseFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate) {

    private val viewModel: ProductDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeProductDetails()
        observeCartQuantity()
    }

    private fun observeProductDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productDetailState.collect { resource ->
                    binding.progressbar.isVisible = resource is Resource.Loading
                    binding.stateErrorGroup.isVisible = resource is Resource.Error
                    binding.scrollView.isVisible = resource is Resource.Success
                    binding.cardActionBar.isVisible = resource is Resource.Success

                    when (resource) {
                        is Resource.Success -> populateUi(resource.data)
                        is Resource.Error -> binding.txtErrorTitle.text = resource.exception.message
                        else -> { /* No-op */
                        }
                    }
                }
            }
        }
    }

    private fun observeCartQuantity() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.quantityInCart.collect { quantity ->
                    val isInCart = quantity > 0
                    binding.btnAddToCart.isVisible = !isInCart
                    binding.layoutCartOperations.isVisible = isInCart
                    binding.txtQuantity.text = quantity.toString()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddToCart.setOnClickListener { viewModel.onIncreaseClicked() }
        binding.btnPlus.setOnClickListener { viewModel.onIncreaseClicked() }
        binding.btnMinus.setOnClickListener { viewModel.onDecreaseClicked() }
        binding.btnRetry.setOnClickListener { viewModel.getProductDetail() }
    }

    private fun populateUi(product: ProductDetail) {
        binding.apply {
            // YENİ: ViewPager2'yi ve indicator'ı kuruyoruz.
            val imageAdapter = ImageViewPagerAdapter(product.images)
            pagerImages.adapter = imageAdapter
            TabLayoutMediator(pagerIndicator, pagerImages) { _, _ ->
                // Bu blok, tab'ları metin veya ikonla özelleştirmek için kullanılır.
                // Biz sadece nokta istediğimiz için boş bırakıyoruz.
            }.attach()

            txtTitle.text = product.title
            txtRatingValue.text = "${product.rating} (${product.ratingCount})"
            chipBrand.text = product.brand
            chipCategory.text = product.category

            txtDiscountedPrice.text = product.formattedDiscountedPrice
            txtOriginalPrice.text = product.formattedPrice
            txtOriginalPrice.paintFlags = if (product.savingsInfo.contains("%")) {
                txtOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                txtOriginalPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            txtSavings.text = product.savingsInfo
            txtDescription.text = product.description
            txtActionPrice.text = product.formattedDiscountedPrice
        }
    }
}