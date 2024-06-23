package com.example.mustafakocer.data.repository

import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.network.IDummyApi
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val api: IDummyApi
) : BaseRepository() {


    /*
      suspend fun getProducts(): Products {
          val response: Response<Products> = api.getProducts()
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

     */

    suspend fun getProductsRepo(): Resource<Products> {
        return safeApiCall {
            api.getProducts()
        }
    }

}