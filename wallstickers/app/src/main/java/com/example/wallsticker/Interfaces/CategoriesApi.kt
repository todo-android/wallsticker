package com.example.wallsticker.Interfaces


import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Image
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface QuoteesCategoriesApi {
    @GET("QuotesCats")
    suspend fun getQuotesCategories(): Response<List<Category>>


}


interface ImageesApi {
    @GET("ImgCats")
    suspend fun getImgesCategories(): Response<List<Category>>

    @GET("latest")
    suspend fun getImages(
        @Query("offset") offset: Int,
        @Query("id") id: Int? = null
    ): Response<List<Image>>
}