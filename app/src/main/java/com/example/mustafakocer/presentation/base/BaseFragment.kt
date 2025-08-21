package com.example.mustafakocer.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * A typealias for the inflater function used to create a [ViewBinding] instance.
 */
typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

/**
 * An abstract base class for Fragments that simplifies ViewBinding setup.
 * This class manages the creation and lifecycle of the binding object, reducing
 * boilerplate code in subclasses.
 *
 * @param VB The type of the ViewBinding class.
 * @param inflate The inflater function for the specific [ViewBinding].
 */
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflater<VB>,
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

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
    }
}