package com.example.wallsticker.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Quote(
    var id: Int?,
    val quote: String?,
    val count_shared: Int?,
    val count_views: Int?,
    var isfav: Int = 0
): Parcelable