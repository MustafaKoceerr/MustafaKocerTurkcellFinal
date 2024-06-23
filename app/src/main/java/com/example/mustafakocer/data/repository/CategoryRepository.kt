package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.Categories
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.network.ICategoryApi
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val api: ICategoryApi
) : BaseRepository() {

    suspend fun getCategoriesRepo(): Resource<Categories> {
        return safeApiCall {
            api.getCategories()
        }

    }


    suspend fun getProductsByCategoryRepo(categoryName: String): Resource<Products> {
        return safeApiCall {
            api.getProductsByCategory(categoryName)
        }
    }
}