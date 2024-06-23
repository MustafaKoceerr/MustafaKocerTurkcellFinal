package com.example.mustafakocer.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mustafakocer.data.model.Dimensions
import com.example.mustafakocer.data.model.Meta
import com.example.mustafakocer.data.model.Review

@Entity(
    tableName = "products"
)
data class SelectedProduct(
    @PrimaryKey(autoGenerate = true)
    val pid : Int, // product local id
    val userId:Int, // UserId
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Long,
    val tags: List<String>,
    val brand: String,
    val sku: String,
    val weight: Long,
    val dimensions: Dimensions,
    val warrantyInformation: String,
    val shippingInformation: String,
    val availabilityStatus: String,
    val reviews: List<Review>,
    val returnPolicy: String,
    val minimumOrderQuantity: Long,
    val meta: Meta,
    val thumbnail: String,
    val images: List<String>
)