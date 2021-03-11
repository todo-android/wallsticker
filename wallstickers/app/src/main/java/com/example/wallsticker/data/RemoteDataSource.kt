package com.example.wallsticker.data

import com.example.wallsticker.Interfaces.WallApi
import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Model.Images
import com.example.wallsticker.Model.Quotes
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val wallApi: WallApi
) {

    suspend fun getImages(): Response<Images> {
        return wallApi.getImages()
    }

    suspend fun getCategories(): Response<Categories> {
        return wallApi.getImgesCategories()
    }

    suspend fun getQuotes(): Response<Quotes> {
        return wallApi.getQuotes()
    }

    suspend fun getCategoriesQuotes():Response<Categories>{
        return wallApi.getQuotesCategories()
    }


}