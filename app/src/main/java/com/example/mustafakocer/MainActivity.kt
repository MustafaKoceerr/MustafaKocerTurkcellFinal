package com.example.mustafakocer

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.repository.NetworkRepository
import com.example.mustafakocer.databinding.ActivityMainBinding
import com.example.mustafakocer.viewmodel.HomeViewModel
import com.example.mustafakocer.viewmodel.factory.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var factory: HomeViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val api = IDummyApi()
        val repository = NetworkRepository(api)
        factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.btnDeneme.setOnClickListener {
            viewModel.getProducts()
        }
        Log.d("products", "okey")

        subscribeToObservables2()
    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            viewModel.statedFlowProducts.collectLatest {
                // buradan alıp, recycler view adapter'a bağlayacağım
                it?.let { bigproducts ->
                    bigproducts.products.forEach {
                        Log.d("products", "$it")
                    }
                }
            }
        }
    }

    private fun subscribeToObservables2() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statedFlowProducts.collectLatest {
                    // buradan alıp, recycler view adapter'a bağlayacağım
                    it?.let { bigproducts ->
                        bigproducts.products.forEach {
                            Log.d("products", "$it")
                        }
                    }
                }
            }
        }
    }
}