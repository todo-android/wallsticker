package com.example.wallsticker.Model

import com.google.gson.annotations.SerializedName

data class Quotes(
    @SerializedName("qoutes")
    val results :List<Quote> )