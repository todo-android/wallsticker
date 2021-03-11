package com.example.wallsticker.Model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    @SerializedName("id")
    val image_id: Int,
    val image_upload: String?,
    val cat_id: Int,
    val view_count: Int,
    val download_count: Int,
    var isfav: Int?

) : Parcelable