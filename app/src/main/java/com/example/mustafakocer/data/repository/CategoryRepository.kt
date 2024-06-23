package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.network.ICategoryApi
import com.example.mustafakocer.data.network.IDummyApi

class CategoryRepository(
    private val api: ICategoryApi
):BaseRepository() {

}