package com.example.wallsticker.Repository

import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Model.Images
import com.example.wallsticker.Retrofit.RetrofitInstance
import com.example.wallsticker.data.LocalDataSource
import com.example.wallsticker.data.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class ImagesRepo @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {
    val remot = remoteDataSource
    val local = localDataSource

    //this for categories Images
    suspend fun getImagescategories(): Response<Categories> {
        return RetrofitInstance.apiImage.getImgesCategories()
    }


    //this for images
    suspend fun getImages(offset: Int, id: Int?): Response<Images> {
        return RetrofitInstance.apiImage.getImages()
    }

    //read images by category


}