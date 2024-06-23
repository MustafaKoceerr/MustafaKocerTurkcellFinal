package com.example.mustafakocer.util

import androidx.room.TypeConverter
import com.example.mustafakocer.data.model.Dimensions
import com.example.mustafakocer.data.model.Meta
import com.example.mustafakocer.data.model.Reviews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    @JvmStatic
    fun fromReviewsList(reviews: List<Reviews>): String {
        return Gson().toJson(reviews)
    }

    @TypeConverter
    @JvmStatic
    fun toReviewsList(reviewsString: String): List<Reviews> {
        val listType = object : TypeToken<List<Reviews>>() {}.type
        return Gson().fromJson(reviewsString, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromDimensions(dimensions: Dimensions?): String? {
        return dimensions?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toDimensions(dimensionsString: String?): Dimensions? {
        return dimensionsString?.let {
            Gson().fromJson(it, Dimensions::class.java)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMeta(meta: Meta?): String? {
        return meta?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toMeta(metaString: String?): Meta? {
        return metaString?.let {
            Gson().fromJson(it, Meta::class.java)
        }
    }

}