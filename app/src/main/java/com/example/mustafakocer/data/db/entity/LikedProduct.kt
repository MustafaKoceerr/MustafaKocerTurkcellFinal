package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mustafakocer.data.model.Dimensions
import com.example.mustafakocer.data.model.Meta
import com.example.mustafakocer.data.model.Reviews
import com.google.gson.annotations.SerializedName

@Entity(tableName = "liked_product")
data class LikedProduct(
    @PrimaryKey(autoGenerate = true)
    val localId: Int,
    val userId: Int,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("price") var price: Double? = null,
    @SerializedName("discountPercentage") var discountPercentage: Double? = null,
    @SerializedName("rating") var rating: Double? = null,
    @SerializedName("stock") var stock: Int? = null,
    @SerializedName("tags") var tags: List<String> = mutableListOf(),
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("sku") var sku: String? = null,
    @SerializedName("weight") var weight: Int? = null,
    @SerializedName("dimensions") var dimensions: Dimensions? = Dimensions(),
    @SerializedName("warrantyInformation") var warrantyInformation: String? = null,
    @SerializedName("shippingInformation") var shippingInformation: String? = null,
    @SerializedName("availabilityStatus") var availabilityStatus: String? = null,
    @SerializedName("reviews") var reviews: List<Reviews> = mutableListOf(),
    @SerializedName("returnPolicy") var returnPolicy: String? = null,
    @SerializedName("minimumOrderQuantity") var minimumOrderQuantity: Int? = null,
    @SerializedName("meta") var meta: Meta? = Meta(),
    @SerializedName("images") var images: List<String> = mutableListOf(),
    @SerializedName("thumbnail") var thumbnail: String? = null

)
