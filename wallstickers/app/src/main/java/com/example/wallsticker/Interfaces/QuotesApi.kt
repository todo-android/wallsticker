package com.example.wallsticker.Interfaces


import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Quote
import com.example.wallsticker.Model.Quotes
import retrofit2.Response
import retrofit2.http.*


//get quotes latest
interface QuotesApi {
    @GET("QuotesCats")
    suspend fun getQuotesCategories(): Response<Categories>

    @GET("quotes")
    suspend fun getQuotes(
    ): Response<Quotes>

}




