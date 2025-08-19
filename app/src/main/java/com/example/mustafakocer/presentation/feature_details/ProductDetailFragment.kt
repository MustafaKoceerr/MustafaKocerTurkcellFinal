package com.example.mustafakocer.presentation.feature_details

import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentProductDetailBinding
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.model.Review
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.mustafakocer.presentation.common.UiErrorMapper
import com.example.mustafakocer.presentation.feature_details.util.enableAutoRepeat
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment :
    BaseFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate) {

    private val viewModel: ProductDetailViewModel by viewModels()
    private var pagerMediator: TabLayoutMediator? = null

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper

    // --- BaseFragment Implementasyonu ---
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }

    override fun onRetry() {
        viewModel.getProductDetail()
    }
    // ------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeProductDetails()
        observeCartQuantity()
        observeDescriptionState()
    }

    private fun observeProductDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productDetailState.collect { resource ->
                    val isLoading = resource is Resource.Loading
                    val isError = resource is Resource.Error
                    val isSuccess = resource is Resource.Success

                    // Görünürlükleri yönet
                    binding.viewLoadingStub.isVisible = isLoading
                    binding.contentView.isVisible = isSuccess
                    binding.cardActionBar.isVisible = isSuccess

                    if (isError) {
                        handleErrorState(
                            binding.viewErrorStub,
                            (resource as Resource.Error).exception
                        )
                    } else {
                        hideErrorState()
                    }

                    if (isSuccess) {
                        populateUi((resource as Resource.Success).data)
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
        binding.btnPlus.enableAutoRepeat { viewModel.onIncreaseClicked() }
        binding.btnMinus.enableAutoRepeat { viewModel.onDecreaseClicked() }
        binding.btnToggleDescription.setOnClickListener { viewModel.onToggleDescription() }
    }

    private companion object {
        private const val COLLAPSED_MAX_LINES = 3

    }

    private fun populateUi(product: ProductDetail) {
        binding.apply {
            // 1. Resim Pager'ını ayarla
            setupPager(product.images)

            // 2. Başlık, Rating ve ana etiketler
            txtTitle.text = product.title
            txtRatingValue.text = "${product.rating} (${product.ratingCount})"
            updateChips(
                chipGroupMeta,
                product.tags
            ) // Değişti: Artık ChipGroup'u parametre olarak alıyor

            setupReviews(product.reviews)

            // 3. Fiyat Bilgileri
            txtDiscountedPrice.text = product.formattedDiscountedPrice
            txtOriginalPrice.text = product.formattedPrice
            txtOriginalPrice.paintFlags = if (product.savingsInfo.contains("%")) {
                txtOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                txtOriginalPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            txtSavings.text = product.savingsInfo

            // 5. Açıklama Alanı
            txtDescription.text = product.description
            // Başlangıç durumu: daraltılmış ve sonunda üç nokta (...) var
            txtDescription.maxLines = COLLAPSED_MAX_LINES
            txtDescription.ellipsize = TextUtils.TruncateAt.END

            // View çizilmeden hemen önce, metnin gerçekten kısaltılıp kısaltılmadığını kontrol et.
            // Eğer metin zaten kısa ise "Devamını Oku" butonunu göstermeye gerek yok.
            txtDescription.doOnPreDraw {
                val layout = txtDescription.layout
                val needsToggle = layout != null &&
                        layout.lineCount > 0 &&
                        layout.getEllipsisCount(layout.lineCount - 1) > 0

                btnToggleDescription.isVisible = needsToggle
            }

            // 6. Alt Aksiyon Barındaki Fiyatı Güncelle
            txtActionPrice.text = product.formattedDiscountedPrice
        }
    }


    /**
     * DEĞİŞTİ: Bu metot artık daha genel amaçlı.
     * Hangi ChipGroup'u ve hangi veri listesini dolduracağını parametre olarak alır.
     * Bu, kod tekrarını önler ve metodun yeniden kullanılabilirliğini artırır.
     */
    private fun updateChips(chipGroup: ChipGroup, tags: List<String>) {
        val inflater = LayoutInflater.from(chipGroup.context)
        chipGroup.removeAllViews() // Yeni chipleri eklemeden önce eskileri temizle.

        tags.forEach { tag ->
            if (tag.isNotBlank()) {
                val chip = inflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip
                chip.text = tag
                chipGroup.addView(chip)
            }
        }
    }

    private fun observeDescriptionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isDescriptionExpanded.collect { isExpanded ->
                    // 1) Boyut animasyonu: ChangeBounds
                    val changeBounds = ChangeBounds().apply {
                        duration = 350L
                        interpolator = AnimationUtils.loadInterpolator(
                            requireContext(),
                            android.R.interpolator.fast_out_slow_in // MD easing
                        )
                    }

                    // 2) Hafifçe fade (buton/ellipsis gibi öğeler için)
                    val fade = Fade(Fade.IN or Fade.OUT).apply {
                        duration = 200L
                    }

                    val transition = TransitionSet().apply {
                        ordering = TransitionSet.ORDERING_TOGETHER
                        addTransition(changeBounds)
                        addTransition(fade)
                    }

                    TransitionManager.beginDelayedTransition(binding.cardDescription, transition)

                    // İçerik güncellemesi (sadece maxLines değiştiriyoruz)
                    if (isExpanded) {
                        binding.txtDescription.maxLines = Int.MAX_VALUE
                        binding.btnToggleDescription.setText(R.string.pd_action_show_less)
                        binding.btnToggleDescription.setIconResource(R.drawable.ic_expand_less_24)
                    } else {
                        binding.txtDescription.maxLines = COLLAPSED_MAX_LINES
                        binding.btnToggleDescription.setText(R.string.pd_action_read_more)
                        binding.btnToggleDescription.setIconResource(R.drawable.ic_expand_more_24)
                    }
                }
            }
        }
    }


    // DEĞİŞTİ: Bu metodu doğru haline geri getiriyoruz.
    private fun setupPager(images: List<String>) {
        val adapter = ImageViewPagerAdapter(images)
        binding.pagerImages.adapter = adapter

        binding.pagerIndicator.isVisible = images.size > 1

        pagerMediator?.detach()

        // TabLayout <-> ViewPager2 bağla ve her taba bizim küçük "dot" view'ımızı ver
        pagerMediator = TabLayoutMediator(binding.pagerIndicator, binding.pagerImages) { tab, _ ->
            tab.customView = createDotView()
        }.also { it.attach() }
    }

    // YENİDEN EKLENDİ: Bu fonksiyonu geri getiriyoruz.
    private fun createDotView(): View {
        val ctx = requireContext()
        // Boyutları ve aralıkları R.dimen'den alıyoruz, bu harika bir pratik.
        val size = resources.getDimensionPixelSize(R.dimen.pd_pager_dot_size)
        val margin = resources.getDimensionPixelSize(R.dimen.pd_pager_dot_spacing)
        return View(ctx).apply {
            layoutParams = ViewGroup.MarginLayoutParams(size, size).also {
                it.setMargins(margin, margin, margin, margin)
            }
            background = AppCompatResources.getDrawable(ctx, R.drawable.bg_pd_pager_dot_selector)
            // Erişilebilirlik için bu view'ın önemli olmadığını belirtiyoruz.
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            contentDescription = null
        }
    }


    private fun setupReviews(reviews: List<Review>) {
        // Senaryo Yönetimi: Yorum listesi boş mu?
        val hasReviews = reviews.isNotEmpty()

        // Eğer yorum yoksa, başlık ve RecyclerView'dan oluşan tüm bölümü gizle.
        // Bu, "Worst Case" senaryosunu yönetir ve kullanıcıya boş bir alan göstermez.
        binding.rowReviewsHeader.isVisible = hasReviews
        binding.recyclerReviews.isVisible = hasReviews

        // Sadece gösterilecek yorum varsa adapter'ı ve layout manager'ı ayarla.
        if (hasReviews) {
            // Not: Detay ekranında genellikle ilk birkaç yorum gösterilir.
            // API'den zaten filtrelenmiş geldiğini varsayıyoruz.
            // Eğer tüm liste geliyorsa, burada .take(3) gibi bir mantık eklenebilir.
            val reviewAdapter = ReviewAdapter(reviews)

            binding.recyclerReviews.apply {
                // RecyclerView'ın satırları nasıl dizeceğini belirtir.
                // Dikey bir liste için LinearLayoutManager kullanıyoruz.
                layoutManager = LinearLayoutManager(requireContext())
                adapter = reviewAdapter
                // NestedScrollView içinde olduğumuz için, RecyclerView'ın kendi scroll
                // olaylarını devre dışı bırakmak performansı artırır ve takılmaları önler.
                // Bu zaten XML'de `nestedScrollingEnabled="false"` ile yapıldı ama
                // koddan da yönetmek iyi bir pratiktir.
                isNestedScrollingEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        pagerMediator?.detach()
        pagerMediator = null
        super.onDestroyView()
    }

}