package com.example.wallsticker.Model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(

    @SerializedName("id", alternate = ["cid"])
    val id: Int,
    @SerializedName("category_name", alternate = ["name"])
    val name: String,
    @SerializedName("category_image", alternate = ["image"])
    val image: String,

    val total_wallpaper: String?

): Parcelable