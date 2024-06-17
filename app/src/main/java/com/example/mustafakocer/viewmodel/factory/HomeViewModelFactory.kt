package com.example.mustafakocer.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mustafakocer.data.repository.NetworkRepository
import com.example.mustafakocer.viewmodel.HomeViewModel

class HomeViewModelFactory(
    private val repository: NetworkRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }

}
