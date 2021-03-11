package com.example.wallsticker.Interfaces


import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Model.Images
import com.example.wallsticker.Model.Quotes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WallApi {

    @GET("ImgCats")
    suspend fun getImgesCategories(): Response<Categories>

    @GET("latest")
    suspend fun getImages(
        @Query("id") id: Int? = null
    ): Response<Images>

    @GET("QuotesCats")
    suspend fun getQuotesCategories(): Response<Categories>

    @GET("quotes")
    suspend fun getQuotes(
    ): Response<Quotes>

}