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
import com.example.mustafakocer.presentation.feature_details.util.enableAutoRepeat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Displays the detailed information for a single product.
 * This fragment observes multiple state flows from [ProductDetailViewModel] to build a complex,
 * dynamic UI with animations and interactive elements.
 */
@AndroidEntryPoint
class ProductDetailFragment :
    BaseFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate) {

    private val viewModel: ProductDetailViewModel by viewModels()
    private var pagerMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    /**
     * Subscribes to all relevant StateFlows from the ViewModel to update the UI
     * in a lifecycle-aware manner.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.productDetailState.collect(::handleProductDetailState) }
                launch { viewModel.quantityInCart.collect(::handleCartQuantityState) }
                launch { viewModel.isDescriptionExpanded.collect(::handleDescriptionExpandedState) }
            }
        }
    }

    /**
     * Sets up all click and touch listeners for the fragment's views.
     */
    private fun setupClickListeners() {
        binding.stateLayout.onRetry = { viewModel.onRetry() }
        binding.btnAddToCart.setOnClickListener { viewModel.onIncreaseClicked() }
        binding.btnPlus.enableAutoRepeat { viewModel.onIncreaseClicked() }
        binding.btnMinus.enableAutoRepeat { viewModel.onDecreaseClicked() }
        binding.contentView.findViewById<View>(R.id.btnToggleDescription).setOnClickListener {
            viewModel.onToggleDescription()
        }
    }

    /**
     * Handles updates to the main product detail resource, showing loading, error, or content states.
     */
    private fun handleProductDetailState(resource: Resource<ProductDetail>) {
        binding.cardActionBar.isVisible = resource is Resource.Success
        when (resource) {
            is Resource.Loading -> binding.stateLayout.showLoading()
            is Resource.Error -> binding.stateLayout.showError(subtitle = resource.exception.message)
            is Resource.Success -> {
                populateUi(resource.data)
                binding.stateLayout.showContent()
            }
            is Resource.Idle -> { /* No-op */ }
        }
    }

    /**
     * Handles updates to the quantity of the product in the cart, toggling UI elements accordingly.
     */
    private fun handleCartQuantityState(quantity: Int) {
        val isInCart = quantity > 0
        binding.btnAddToCart.isVisible = !isInCart
        binding.layoutCartOperations.isVisible = isInCart
        binding.txtQuantity.text = quantity.toString()
    }

    /**
     * Handles the expanded/collapsed state of the description text, applying animations.
     */
    private fun handleDescriptionExpandedState(isExpanded: Boolean) {
        val transition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(
                ChangeBounds().apply {
                    duration = 350L
                    interpolator =
                        AnimationUtils.loadInterpolator(
                            requireContext(),
                            android.R.interpolator.fast_out_slow_in
                        )
                }
            )
            addTransition(Fade(Fade.IN or Fade.OUT).apply { duration = 200L })
        }
        TransitionManager.beginDelayedTransition(binding.cardDescription, transition)

        binding.txtDescription.maxLines = if (isExpanded) Int.MAX_VALUE else 3
        binding.btnToggleDescription.setText(
            if (isExpanded) R.string.pd_action_show_less else R.string.pd_action_read_more
        )
        binding.btnToggleDescription.setIconResource(
            if (isExpanded) R.drawable.ic_expand_less_24 else R.drawable.ic_expand_more_24
        )
    }

    /**
     * Populates the main content area with data from the [ProductDetail] object.
     */
    private fun populateUi(product: ProductDetail) {
        binding.apply {
            setupPager(product.images)
            txtTitle.text = product.title
            txtRatingValue.text =
                getString(R.string.pd_rating_format, product.rating, product.ratingCount)
            updateChips(chipGroupMeta, product.tags)
            setupPricing(product)
            setupDescription(product)
            setupReviews(product.reviews)
            txtActionPrice.text = product.formattedDiscountedPrice
        }
    }

    private fun setupPricing(product: ProductDetail) {
        binding.apply {
            txtDiscountedPrice.text = product.formattedDiscountedPrice
            txtOriginalPrice.text = product.formattedPrice
            txtSavings.text = product.savingsInfo
            txtOriginalPrice.paintFlags = if (product.savingsInfo.contains("%")) {
                txtOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                txtOriginalPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    private fun setupDescription(product: ProductDetail) {
        binding.apply {
            txtDescription.text = product.description
            txtDescription.maxLines = 3
            txtDescription.ellipsize = TextUtils.TruncateAt.END
            txtDescription.doOnPreDraw {
                val layout = txtDescription.layout
                btnToggleDescription.isVisible =
                    layout != null && layout.lineCount > 0 && layout.getEllipsisCount(layout.lineCount - 1) > 0
            }
        }
    }

    private fun updateChips(chipGroup: ChipGroup, tags: List<String>) {
        val inflater = LayoutInflater.from(chipGroup.context)
        chipGroup.removeAllViews()
        tags.forEach { tag ->
            if (tag.isNotBlank()) {
                (inflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip).also {
                    it.text = tag
                    chipGroup.addView(it)
                }
            }
        }
    }

    private fun setupPager(images: List<String>) {
        binding.pagerImages.adapter = ImageViewPagerAdapter(images)
        binding.pagerIndicator.isVisible = images.size > 1
        pagerMediator?.detach()
        pagerMediator = TabLayoutMediator(binding.pagerIndicator, binding.pagerImages) { tab, _ ->
            tab.customView = createDotView()
        }.also { it.attach() }
    }

    private fun createDotView(): View {
        val ctx = requireContext()
        val size = resources.getDimensionPixelSize(R.dimen.pd_pager_dot_size)
        val margin = resources.getDimensionPixelSize(R.dimen.pd_pager_dot_spacing)
        return View(ctx).apply {
            layoutParams = ViewGroup.MarginLayoutParams(size, size).also {
                it.setMargins(margin, margin, margin, margin)
            }
            background = AppCompatResources.getDrawable(ctx, R.drawable.bg_pd_pager_dot_selector)
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }

    private fun setupReviews(reviews: List<Review>) {
        val hasReviews = reviews.isNotEmpty()
        binding.rowReviewsHeader.isVisible = hasReviews
        binding.recyclerReviews.isVisible = hasReviews
        if (hasReviews) {
            binding.recyclerReviews.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ReviewAdapter(reviews)
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