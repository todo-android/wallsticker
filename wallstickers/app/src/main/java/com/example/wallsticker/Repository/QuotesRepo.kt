package com.example.wallsticker.Repository

import com.example.wallsticker.Model.*
import com.example.wallsticker.Retrofit.RetrofitInstance
import com.example.wallsticker.data.LocalDataSource
import com.example.wallsticker.data.RemoteDataSource

import retrofit2.Response
import javax.inject.Inject

class QuotesRepo @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {
    val remot=remoteDataSource
    val local=localDataSource


    //this for categories Images
    suspend fun getQuotesCategories(): Response<Categories> {
        return  RetrofitInstance.apiQuotes.getQuotesCategories()
    }


    //this for images
    suspend fun getQuotes(): Response<Quotes> {
        return  RetrofitInstance.apiQuotes.getQuotes()
    }

}