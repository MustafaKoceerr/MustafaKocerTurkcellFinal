package com.example.mustafakocer.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.LayoutStateErrorBinding
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.presentation.common.UiErrorMapper

typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflater<VB>,
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    protected abstract val uiErrorMapper: UiErrorMapper
    protected abstract fun onRetry()

    // --- ViewBinding Yaşam Döngüsü ---
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // DEĞİŞTİ: Fragment view'ı yok edildiğinde, inflate edilmiş referansı da temizle.
        // Bu, bir sonraki onCreateView'de ViewStub'ın yeniden inflate edilmesini sağlar.
        inflatedErrorView = null
    }

    // --- ViewStub Destekli Yardımcı Fonksiyonlar ---

    private var inflatedErrorView: View? = null

    /**
     * Handles the error state by inflating the ViewStub (if not already inflated)
     * and populating it with meaningful error data.
     */
    protected fun handleErrorState(errorStub: ViewStub, exception: AppException) {
        // DÜZELTME: `nil` yerine `null` kullanılıyor.
        if (inflatedErrorView == null) {
            inflatedErrorView = errorStub.inflate()
        }

        val errorBinding = LayoutStateErrorBinding.bind(inflatedErrorView!!)

        inflatedErrorView?.isVisible = true

        val uiError = uiErrorMapper.map(exception)
        errorBinding.imgErrorIcon.setImageResource(uiError.icon)
        errorBinding.txtErrorTitle.setText(uiError.title)
        errorBinding.txtErrorSubtitle.setText(uiError.subtitle)
        errorBinding.btnRetry.setText(uiError.retryButtonText)
        errorBinding.btnRetry.setOnClickListener { onRetry() }
    }

    /**
     * Hata durumu ortadan kalktığında, inflate edilmiş hata view'ını gizler.
     */
    protected fun hideErrorState() {
        inflatedErrorView?.isVisible = false
    }
}