package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.databinding.FragmentOrderBinding
import com.example.mustafakocer.ui.home.adapter.CartProductAdapter
import com.example.mustafakocer.ui.home.adapter.OrderProductAdapter
import com.example.mustafakocer.ui.home.adapter.ProductAdapter2
import com.example.mustafakocer.ui.home.viewmodel.HomeViewModel
import com.example.mustafakocer.ui.home.viewmodel.OrderViewModel
import com.example.mustafakocer.util.UserId
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val viewModel: OrderViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    // // todo api'den order çekilecek
        binding.orderRecyclerView.also { recyclerView ->
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        }
        viewModel.getOrder("6")
        // todo normalde burayı user id ile çekecektim ama api sadece id=6 olan
        // todo kullanıcı için veri döndürdüğü için 6 numaralı user'ı aldım
        observeOrder()

    }


    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOrderBinding {
        return FragmentOrderBinding.inflate(inflater,container,false)
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
                        // Update UI with products data
                        // todo recyler view DESING YAP
                        Toast.makeText(
                            requireContext(),
                            "Order Geldi",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("flow", "${resource.value}")
                            val temp = resource.value.carts
                        Log.d("temp", "listem : ${temp.size}")

                        binding.orderRecyclerView.adapter =
                            OrderProductAdapter(temp)


                        // her veri geldiğinde db'ye bakıyor ve oradan karşılaştırıyor
                    }

                    is Resource.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Hata ${resource.errorCode}  ${resource.errorBody}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    is Resource.Waiting -> {
                        binding.progressbar.visibleProgressBar(false)
                    }
                }

            }

        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}