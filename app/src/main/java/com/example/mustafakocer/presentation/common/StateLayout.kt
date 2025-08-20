package com.example.mustafakocer.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.ViewStateLayoutBinding // Oluşturulan Binding Sınıfı

class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewStateLayoutBinding

    private var contentView: View? = null
    var onRetry: (() -> Unit)? = null

    init {
        // DOĞRU YÖNTEM:
        // 1. Önce layout'u bu FrameLayout'a inflate et.
        val view = LayoutInflater.from(context).inflate(R.layout.view_state_layout, this, true)
        // 2. Sonra bu inflate edilmiş view'ı binding'e bağla.
        binding = ViewStateLayoutBinding.bind(view)

        initializeAttributes(attrs)

        binding.stateErrorView.stateErrorRetryButton.setOnClickListener {
            onRetry?.invoke()
        }
    }

    /**
     * Bu metod, Android'in XML'deki view'ları inflate edip bu custom view'a
     * çocuk (child) olarak eklemesi bittikten sonra çağrılır.
     * Bizim Content'imiz (örn. RecyclerView), bu custom view'ın bir çocuğu olacağı için
     * onu burada bulup referansını alıyoruz.
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        // StateLayout'ın içine eklenen ve bizim state view'larımız olmayan ilk view'ı
        // `contentView` olarak kabul ediyoruz.
        if (childCount > 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child !is StateLayout) { // Kendisi olamaz
                    val isStateView = child.id == R.id.state_loading_view ||
                            child.id == R.id.state_error_view ||
                            child.id == R.id.state_empty_view ||
                            child.id == R.id.state_prompt_view
                    if (!isStateView) {
                        contentView = child
                        break
                    }
                }
            }
        }
        // Başlangıçta tüm view'ları gizleyelim. Hangi view'ın gösterileceğine
        // Fragment/Activity karar verecek.
        hideAllViews()
    }

    /**
     * Tüm olası durumları (content dahil) gizlemek için yardımcı fonksiyon.
     * Her `show...` metodunun başında çağrılarak temiz bir başlangıç sağlar.
     */
    private fun hideAllViews() {
        binding.stateLoadingView.root.visibility = GONE
        binding.stateErrorView.root.visibility = GONE
        binding.stateEmptyView.root.visibility = GONE
        binding.statePromptView.root.visibility = GONE
        contentView?.visibility = GONE
    }

    // --- PUBLIC METODLAR ---

    fun showLoading() {
        hideAllViews()
        binding.stateLoadingView.root.visibility = VISIBLE
    }

    fun showContent() {
        hideAllViews()
        contentView?.visibility = VISIBLE
    }

    /**
     * Hata durumunu gösterir. Opsiyonel olarak başlık ve alt başlık metinlerini
     * programatik olarak değiştirebilir.
     */
    fun showError(title: String? = null, subtitle: String? = null) {
        hideAllViews()
        title?.let { binding.stateErrorView.stateErrorTitle.text = it }
        subtitle?.let { binding.stateErrorView.stateErrorSubtitle.text = it }
        binding.stateErrorView.root.visibility = VISIBLE
    }

    /**
     * Boş sonuç durumunu gösterir. Opsiyonel olarak metinleri değiştirebilir.
     */
    fun showEmpty(title: String? = null, subtitle: String? = null) {
        hideAllViews()
        title?.let { binding.stateEmptyView.stateEmptyTitle.text = it }
        subtitle?.let { binding.stateEmptyView.stateEmptySubtitle.text = it }
        binding.stateEmptyView.root.visibility = VISIBLE
    }

    /**
     * Başlangıç/İpucu durumunu gösterir. Opsiyonel olarak metinleri değiştirebilir.
     */
    fun showPrompt(title: String? = null, subtitle: String? = null) {
        hideAllViews()
        title?.let { binding.statePromptView.statePromptTitle.text = it }
        subtitle?.let { binding.statePromptView.statePromptSubtitle.text = it }
        binding.statePromptView.root.visibility = VISIBLE
    }

    // `initializeAttributes` metodu öncekiyle aynı, burada bir değişiklik yok.
    private fun initializeAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.StateLayout, 0, 0).apply {
            try {
                // Prompt State
                getResourceId(R.styleable.StateLayout_promptIcon, -1).takeIf { it != -1 }?.let {
                    binding.statePromptView.statePromptIcon.setImageResource(it)
                }
                getString(R.styleable.StateLayout_promptTitle)?.let {
                    binding.statePromptView.statePromptTitle.text = it
                }
                getString(R.styleable.StateLayout_promptSubtitle)?.let {
                    binding.statePromptView.statePromptSubtitle.text = it
                }

                // Empty State
                getResourceId(R.styleable.StateLayout_emptyIcon, -1).takeIf { it != -1 }?.let {
                    binding.stateEmptyView.stateEmptyIcon.setImageResource(it)
                }
                getString(R.styleable.StateLayout_emptyTitle)?.let {
                    binding.stateEmptyView.stateEmptyTitle.text = it
                }
                getString(R.styleable.StateLayout_emptySubtitle)?.let {
                    binding.stateEmptyView.stateEmptySubtitle.text = it
                }

                // Error State
                getResourceId(R.styleable.StateLayout_errorIcon, -1).takeIf { it != -1 }?.let {
                    binding.stateErrorView.stateErrorIcon.setImageResource(it)
                }
                getString(R.styleable.StateLayout_errorTitle)?.let {
                    binding.stateErrorView.stateErrorTitle.text = it
                }
                getString(R.styleable.StateLayout_errorSubtitle)?.let {
                    binding.stateErrorView.stateErrorSubtitle.text = it
                }

            } finally {
                recycle()
            }
        }
    }
}