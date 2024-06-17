package com.example.mustafakocer.data.repository

import android.util.Log
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.network.IDummyApi
import retrofit2.Response

class NetworkRepository(private val api: IDummyApi) {


    suspend fun getProducts(): Products? {
        Log.d("apiRequest", "getProducts start")

        val response: Response<Products> = api.getProducts()
        Log.d("apiRequest", "getProducts after")

        if (response.isSuccessful){
            response.body()?.let {
                return it
            }
            return null
        }else{
            throw ApiException("Error in getProducts fun inside Network Repository" +
                    " ${response.code().toString()}")
        }

    }


}