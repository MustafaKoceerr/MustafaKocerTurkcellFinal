package com.example.mustafakocer.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.mustafakocer.databinding.LayoutStateErrorBinding
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.presentation.common.UiErrorMapper

/**
 * A type alias for the inflater function used to create a [ViewBinding] instance.
 */
typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

/**
 * An abstract base class for Fragments to streamline [ViewBinding] and provide centralized
 * UI state handling (specifically for error states using a [ViewStub]).
 *
 * @param VB The type of the ViewBinding class.
 * @param inflate The inflater function for the specific [ViewBinding].
 */
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflater<VB>,
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    /**
     * A mapper to convert domain-level [AppException] into a UI-friendly error model.
     * Must be provided by the subclass.
     */
    protected abstract val uiErrorMapper: UiErrorMapper

    /**
     * An action to be executed when the user clicks the "Retry" button on the error screen.
     * Must be implemented by the subclass.
     */
    protected abstract fun onRetry()

    private var inflatedErrorView: View? = null

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
        // Reset the inflated view reference to allow the ViewStub to be re-inflated
        // if the fragment view is created again.
        inflatedErrorView = null
    }

    /**
     * Handles the error state by inflating a [ViewStub] (if not already inflated)
     * and populating it with meaningful error data.
     *
     * @param errorStub The [ViewStub] in the fragment's layout for displaying errors.
     * @param exception The [AppException] to be displayed.
     */
    protected fun handleErrorState(errorStub: ViewStub, exception: AppException) {
        // Use the existing inflated view or inflate the stub if it's the first time.
        // The `let` scope function ensures safe handling of the view.
        (inflatedErrorView ?: errorStub.inflate()).let { view ->
            inflatedErrorView = view // Cache the inflated view.
            view.isVisible = true

            val uiError = uiErrorMapper.map(exception)
            // The `apply` scope function makes configuring the binding more concise.
            LayoutStateErrorBinding.bind(view).apply {
                imgErrorIcon.setImageResource(uiError.icon)
                txtErrorTitle.setText(uiError.title)
                txtErrorSubtitle.setText(uiError.subtitle)
                btnRetry.setText(uiError.retryButtonText)
                btnRetry.setOnClickListener { onRetry() }
            }
        }
    }

    /**
     * Hides the inflated error view when the error state is resolved.
     */
    protected fun hideErrorState() {
        inflatedErrorView?.isVisible = false
    }
}