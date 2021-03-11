package com.example.wallsticker.data.databsae

import androidx.room.TypeConverter
import com.example.wallsticker.Model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class imageTypeConverter {

    var gson = Gson()


    @TypeConverter
    fun imageToString(images: Images): String {
        return gson.toJson(images)
    }

    @TypeConverter
    fun StringToImage(data: String): Images {
        var listType = object : TypeToken<Images>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resultToString(result: Image): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data: String): Image {
        val listType = object : TypeToken<Image>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun CategoryToString(categories: Categories): String {
        return gson.toJson(categories)
    }

    @TypeConverter
    fun StringToCategory(data: String): Categories {
        var listType = object : TypeToken<Categories>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun QuotesToString(quotes: Quotes): String {
        return gson.toJson(quotes)
    }

    @TypeConverter
    fun StringToQuotes(data: String): Quotes {
        var listType = object : TypeToken<Quotes>() {}.type
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun QuoteToString(quote: Quote): String {
        return gson.toJson(quote)
    }

    @TypeConverter
    fun StringToQuote(data: String): Quote {
        var listType = object : TypeToken<Quote>() {}.type
        return gson.fromJson(data, listType)
    }


}