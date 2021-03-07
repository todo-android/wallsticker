package com.example.wallsticker.Model

import com.google.gson.annotations.SerializedName

data class Images(
    @SerializedName("latest")
    val results :List<Image> )