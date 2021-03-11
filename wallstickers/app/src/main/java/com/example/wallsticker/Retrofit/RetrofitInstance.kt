package com.example.wallsticker.Retrofit

import com.example.wallsticker.Interfaces.WallApi
import com.example.wallsticker.Utilities.Const
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Const.apiurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    //for Images cat
    val apiImage: WallApi by lazy {
        retrofit.create(WallApi::class.java)
    }


}