package com.example.wallsticker.Model

import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("cats")
    val results :List<Category> )