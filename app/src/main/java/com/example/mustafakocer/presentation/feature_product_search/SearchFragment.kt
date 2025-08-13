package com.example.mustafakocer.presentation.feature_product_search

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentSearchBinding
import com.example.mustafakocer.domain.usecase.SearchProductsUseCase
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    // ViewAnimator'daki çocukların pozisyonlarını sabit olarak tanımlamak kodu çok okunaklı yapar.
    private companion object {
        private const val CHILD_IDLE = 0
        private const val CHILD_CONTENT = 1
        private const val CHILD_EMPTY = 2
        private const val CHILD_ERROR = 3
        private const val CHILD_LOADING = 4
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupStateSwitcherAnimation() // Bonus: Geçiş animasyonu
        observeSearchResults()
        observeUiState() // Tek ve birleşik UI state yöneticisi
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action = SearchFragmentDirections
                .actionSearchFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }

        binding.searchRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            // Optimizasyonlar:
            setHasFixedSize(true)
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
        // Konfigürasyon değişikliklerinde scroll pozisyonunu korumaya yardımcı olur.
        productListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener { binding.searchView.isIconified = false }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })

        // Retry butonu artık stateErrorGroup içinde, ID'si aynı olduğu için binding çalışır.
        binding.btnRetry.setOnClickListener { productListAdapter.retry() }
    }

    private fun setupStateSwitcherAnimation() {
        // Bonus: ViewAnimator'a yumuşak bir geçiş animasyonu ekleyelim.
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out)
        binding.stateSwitcher.inAnimation = fadeIn
        binding.stateSwitcher.outAnimation = fadeOut
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { pagingData ->
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    // ESKİ observeLoadState VE observeSearchQuery METOTLARI GİTTİ.
    // YERİNE BU TEMİZ VE TEK METOT GELDİ:
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { states ->
                    val query = viewModel.searchQuery.value
                    val isQueryShort = query.length < SearchProductsUseCase.MIN_QUERY_LENGTH
                    val hasItems = productListAdapter.itemCount > 0

                    val refreshState = states.refresh

                    // Sadece ilk yükleme anında (liste boşken) LOADING durumunu göster.
                    val isFirstLoad = refreshState is LoadState.Loading && !hasItems

                    // Sadece liste boşken hata durumunu göster.
                    val isError = refreshState is LoadState.Error && !hasItems

                    // Arama bittiğinde ve hiç sonuç yoksa EMPTY durumunu göster.
                    val isEmpty = !isQueryShort &&
                            (refreshState is LoadState.NotLoading) &&
                            states.append.endOfPaginationReached &&
                            !hasItems

                    // Tek bir `when` bloğu ile doğru çocuğu seç.
                    val childToDisplay = when {
                        isQueryShort -> CHILD_IDLE
                        isFirstLoad -> CHILD_LOADING
                        isError -> CHILD_ERROR
                        isEmpty -> CHILD_EMPTY
                        else -> CHILD_CONTENT // Geriye kalan tüm durumlar içeriği gösterir.
                    }

                    // Sadece gerekliyse `displayedChild`'ı güncelle. Bu küçük bir optimizasyondur.
                    if (binding.stateSwitcher.displayedChild != childToDisplay) {
                        binding.stateSwitcher.displayedChild = childToDisplay
                    }

                    // Eğer boş ekran gösteriliyorsa, aranan kelimeyi de yazdır.
                    if (childToDisplay == CHILD_EMPTY) {
                        binding.txtEmptySubtitle.text =
                            getString(R.string.search_empty_subtitle, query)
                    }
                }
            }
        }
    }
}