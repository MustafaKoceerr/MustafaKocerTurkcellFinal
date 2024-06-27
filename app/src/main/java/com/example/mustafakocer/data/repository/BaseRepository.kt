package com.example.mustafakocer.data.repository

import android.util.Log
import com.example.mustafakocer.data.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                val temp = apiCall.invoke()
                Log.d("Error", "api call senden $temp ")

                // invoke() ile bizim retrofit isteğimizi attık, bunu coroutines içinde
                // çağırdığımıza dikkat et.
                // if it be successful we will take result directly
                // and we will put the result to our Resource class
                Resource.Success(temp)

                // T Login için User class'ıydı, yani Success fonksiyonun T'si de user class'ı olacak
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(
                            isNetworkError = false,
                            throwable.code(),
                            throwable.response()!!.errorBody()
                        )
                    }

                    else -> {
                        Resource.Failure(isNetworkError = true, null, null)
                    }
                }
            }
        }

    }


}
