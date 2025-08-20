package com.example.mustafakocer.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding örneği oluşturmak için kullanılan inflater fonksiyonu için bir typealias.
 */
typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

/**
 * Fragment'lar için ViewBinding kurulumunu kolaylaştıran soyut bir base class.
 * Bu sınıf, binding nesnesinin oluşturulmasını ve yaşam döngüsünü yöneterek
 * alt sınıflardaki boilerplate kodu azaltır.
 *
 * @param VB ViewBinding sınıfının tipi.
 * @param inflate Belirli bir ViewBinding için inflater fonksiyonu.
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