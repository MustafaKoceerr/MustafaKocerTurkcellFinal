package com.example.mustafakocer.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.db.entity.Cart
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.repository.DatabaseRepository
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {


    private val _products = MutableStateFlow<Resource<Products>>(Resource.Waiting)
    val products: StateFlow<Resource<Products>> = _products.asStateFlow()
    fun getProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading
            // Make the API call
            val response = repository.getProductsRepo()
            // assign the value, it can be success or failure
            _products.value = response
        }
    }

    // ekleme işlemi yapılıyor
    suspend fun insertProductDB(likedProduct: LikedProduct) {
            val insertedProductId = databaseRepository.insertProductDBRepo(likedProduct)
            if (insertedProductId != -1L) {
                Log.d(
                    "ProductViewModel",
                    "Product inserted successfully with id: $insertedProductId"
                )
            } else {
                Log.e("ProductViewModel", "Failed to insert product")
                // Burada bir hata durumu ele alınabilir
            }
    }

    // ekleme işlemi yapılıyor
    suspend fun deleteProductDB(userId: Int, productId: Int) {
            val deletedProductRow = databaseRepository.deleteProductDBRepo(userId, productId)
            if (deletedProductRow != 0) {
                Log.d(
                    "ProductViewModel",
                    "Product deleted successfully silinen ürün sayısı : $deletedProductRow"
                )
            } else {
                Log.e("ProductViewModel", "Failed to delete product")
                // Burada bir hata durumu ele alınabilir
            }
        }


    suspend fun getOneProductsDB(userId: Int, productId: Int): LikedProduct? {
            try {
                val product = databaseRepository.getOneProductDB(userId, productId)
                return product
            } catch (e: Exception) {
                // Handle error scenario, e.g., log or show error message
                Log.e("ProductViewModel", "Error fetching favorite products", e)
                return null
            }
        }

    suspend fun gelAllLikedProductsDB(userId: Int): List<LikedProduct>? {
        try {
            val products = databaseRepository.gelAllLikedProductsDB(userId)
            Log.e("ProductViewModel", "Veriler geldi $products", )

            return products
        } catch (e: Exception) {
            // Handle error scenario, e.g., log or show error message
            Log.e("ProductViewModel", "Error fetching favorite products")
            return null
        }
    }
  /*
    // ekleme işlemi yapılıyor
    fun insertProductDB(likedProduct: LikedProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            val insertedProductId = databaseRepository.insertProductDBRepo(likedProduct)
            if (insertedProductId != -1L) {
                Log.d(
                    "ProductViewModel",
                    "Product inserted successfully with id: $insertedProductId"
                )
            } else {
                Log.e("ProductViewModel", "Failed to insert product")
                // Burada bir hata durumu ele alınabilir
            }
        }
    }

    // ekleme işlemi yapılıyor
    fun deleteProductDB(userId: Int, productId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deletedProductRow = databaseRepository.deleteProductDBRepo(userId, productId)
            if (deletedProductRow != 0) {
                Log.d(
                    "ProductViewModel",
                    "Product deleted successfully with id: $deletedProductRow"
                )
            } else {
                Log.e("ProductViewModel", "Failed to delete product")
                // Burada bir hata durumu ele alınabilir
            }
        }
    }
   */

   /*
    private val _oneProduct = MutableStateFlow<LikedProduct?>(null)
    val oneProduct: StateFlow<LikedProduct?> = _oneProduct.asStateFlow()
     fun getOneProductsDB(userId: Int, productId: Int) {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                  val product = databaseRepository.getOneProductDB(userId, productId)
                 _oneProduct.value = product
             } catch (e: Exception) {
                 // Handle error scenario, e.g., log or show error message
                 Log.e("ProductViewModel", "Error fetching favorite products", e)
                throw e
             }
         }
    }
    */



    /*
      private val _favoriteProducts = MutableStateFlow<List<Product>>(emptyList())
      val favoriteProducts: StateFlow<List<Product>> = _favoriteProducts

      fun gelAllLikedProductsDB(userId: Int) {
          viewModelScope.launch {
              try {
                  val products = databaseRepository.gelAllLikedProductsDB(userId)
                  _favoriteProducts.value = products
              } catch (e: Exception) {
                  // Handle error scenario, e.g., log or show error message
                  Log.e("ProductViewModel", "Error fetching favorite products", e)
              }
          }
      }
     */


    // Sepete ekleme işlemi
    suspend fun InsertCart(cart: Cart) {
        try {
            val insertedCartId = databaseRepository.insertCartDBRepo(cart)
            if (insertedCartId != -1L) {
                Log.d("CartViewModel", "Cart inserted successfully with id: $insertedCartId")
            } else {
                Log.e("CartViewModel", "Failed to insert cart")
                // Burada bir hata durumu ele alınabilir
            }
        } catch (e: Exception) {
            // Handle error scenario, e.g., log or show error message
            Log.e("CartViewModel", "Error inserting cart", e)
        }
    }

    // Sepet güncelleme işlemi
    suspend fun UpdateCart(cart: Cart) {
        try {
            val updatedCartNumber = databaseRepository.updateCartDBRepo(cart)
            if (updatedCartNumber != 0) {
                Log.d("CartViewModel", "Cart updated successfully with id: $updatedCartNumber")
            } else {
                Log.e("CartViewModel", "Failed to insert cart")
                // Burada bir hata durumu ele alınabilir
            }
        } catch (e: Exception) {
            // Handle error scenario, e.g., log or show error message
            Log.e("CartViewModel", "Error inserting cart", e)
        }
    }

    // Sepetten silme işlemi
    suspend fun DeleteCart(userId: Int, productId: Int) {
        try {
            val deletedRowCount = databaseRepository.deleteCartDBRepo(userId, productId)
            if (deletedRowCount != 0) {
                Log.d("CartViewModel", "Cart deleted successfully. Deleted row count: $deletedRowCount")
            } else {
                Log.e("CartViewModel", "Failed to delete cart")
                // Burada bir hata durumu ele alınabilir
            }
        } catch (e: Exception) {
            // Handle error scenario, e.g., log or show error message
            Log.e("CartViewModel", "Error deleting cart", e)
        }
    }

    // Sepetten silme işlemi
    suspend fun GetOneCart(userId: Int, productId: Int): Cart? {
        val cart = databaseRepository.getOneCartDBRepo(userId, productId)
        return cart
    }

    // todo bu suspend olmayacak, çünkü cart kısmına geçtiğim zaman bunu dinlemem lazım.
    // Kullanıcının tüm sepet ürünlerini getirme işlemi
    suspend fun GetAllCarts(userId: Int): List<Cart>? {
        try {
            val carts = databaseRepository.gelAllCartsDBRepo(userId)
            Log.d("CartViewModel", "Fetched carts: $carts")
            return carts
        } catch (e: Exception) {
            // Handle error scenario, e.g., log or show error message
            Log.e("CartViewModel", "Error fetching carts", e)
            return null
        }
    }


}
