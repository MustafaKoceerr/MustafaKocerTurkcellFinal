package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mustafakocer.databinding.FragmentCategoryBinding
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {

    // ViewModel'i Hilt ile alıyoruz.
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryListAdapter: CategoryListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCategories()
    }

    private fun setupRecyclerView() {
        // DEĞİŞTİ: Tıklama olayı artık tüm Category nesnesini alıyor.
        categoryListAdapter = CategoryListAdapter { category ->
            // Gelen category nesnesini kullanarak navigasyonu çağır.
            navigateToProductsByCategory(category)
        }
        binding.categoryRecyclerView.apply {
            adapter = categoryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel'den kategori listesinin durumunu dinle.
                viewModel.categoriesState.collect { resource ->
                    // Yüklenme durumuna göre ProgressBar'ı yönet.
                    binding.progressbar.isVisible = resource is Resource.Loading

                    when (resource) {
                        is Resource.Success -> {
                            // Başarılı olursa, veriyi adaptöre gönder.
                            categoryListAdapter.submitList(resource.data)
                        }

                        is Resource.Error -> {
                            // Hata olursa, kullanıcıya bir mesaj göster.
                            Toast.makeText(
                                requireContext(),
                                resource.exception.message ?: "Bir hata oluştu",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            // Idle veya Loading durumu için ek bir işlem yapmıyoruz.
                        }
                    }
                }
            }
        }
    }

    // DEĞİŞTİ: Fonksiyon artık Category nesnesi alıyor.
    private fun navigateToProductsByCategory(category: Category) {
        // Safe Args kullanarak ve İKİ argümanı da geçirerek navigasyonu tetikle.
        val action = CategoryFragmentDirections.actionCategoryFragmentToProductsByCategoryFragment(
            categoryName = category.slug,
            categoryDisplayName = category.name
        )
        findNavController().navigate(action)
    }

}