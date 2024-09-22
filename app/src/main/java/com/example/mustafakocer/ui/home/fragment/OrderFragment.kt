package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentOrderBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.adapter.OrderProductAdapter
import com.example.mustafakocer.ui.home.viewmodel.OrderViewModel
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>() {

    private val viewModel: OrderViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.orderRecyclerView.also { recyclerView ->
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        }
        viewModel.getOrder("6")
        // todo normalde burayı user id ile çekecektim ama api sadece id=6 olan kullanıcı için veri döndürdüğü için 6 numaralı user'ı aldım
        observeOrder()

    }

    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOrderBinding {
        return FragmentOrderBinding.inflate(inflater, container, false)
    }

    private fun observeOrder() {
        viewLifecycleOwner.lifecycleScope.launch {
            /*
            When collecting from a StateFlow in your UI, you should typically use Dispatchers.Main.
            This is because you usually want to update the UI in response to the collected state,
            and all UI updates must occur on the main thread.
             */
            viewModel.order.collectLatest { resource ->
                binding.progressbar.visibleProgressBar(false)

                when (resource) {
                    is Resource.Loading -> {
                        // Show loading indicator
                        binding.progressbar.visibleProgressBar(true)
                    }

                    is Resource.Success -> {
                        val temp = resource.value.carts

                        binding.orderRecyclerView.adapter =
                            OrderProductAdapter(temp)
                        // her veri geldiğinde db'ye bakıyor ve oradan karşılaştırıyor
                    }

                    is Resource.Failure -> {

                    }

                    is Resource.Waiting -> {
                        binding.progressbar.visibleProgressBar(false)
                    }
                }

            }

        }
    }

}