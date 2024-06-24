package com.example.mustafakocer.data.db

import androidx.room.TypeConverter
import com.example.mustafakocer.data.model.Dimensions
import com.example.mustafakocer.data.model.Meta
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.data.model.Reviews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromReviewsList(reviews: List<Reviews>): String {
        return Gson().toJson(reviews)
    }

    @TypeConverter
    fun toReviewsList(reviewsString: String): List<Reviews> {
        val listType = object : TypeToken<List<Reviews>>() {}.type
        return Gson().fromJson(reviewsString, listType)
    }

    @TypeConverter
    fun fromDimensions(dimensions: Dimensions?): String? {
        return dimensions?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    fun toDimensions(dimensionsString: String?): Dimensions? {
        return dimensionsString?.let {
            Gson().fromJson(it, Dimensions::class.java)
        }
    }

    @TypeConverter
    fun fromMeta(meta: Meta?): String? {
        return meta?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    fun toMeta(metaString: String?): Meta? {
        return metaString?.let {
            Gson().fromJson(it, Meta::class.java)
        }
    }

    @TypeConverter
    fun fromProduct(product: Product): String {
        return Gson().toJson(product)
    }

    @TypeConverter
    fun toProduct(productString: String): Product {
        val productType = object : TypeToken<Product>() {}.type
        return Gson().fromJson(productString, productType)
    }
}