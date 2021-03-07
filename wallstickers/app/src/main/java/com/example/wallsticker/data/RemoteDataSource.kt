package com.example.wallsticker.data

import com.example.wallsticker.Interfaces.ImagesApi
import com.example.wallsticker.Interfaces.QuotesApi
import com.example.wallsticker.Model.Images
import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Model.Quotes
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val imagesApi: ImagesApi,
    private val quotesApi: QuotesApi
) {

    suspend fun getImages(): Response<Images> {
        return  imagesApi.getImages()
    }

    suspend fun getCategories(): Response<Categories> {
        return  imagesApi.getImgesCategories()
    }

    suspend fun getQuotes():Response<Quotes>{
        return quotesApi.getQuotes()
    }


}