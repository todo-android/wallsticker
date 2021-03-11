package com.example.wallsticker.Model

import com.google.gson.annotations.SerializedName

data class Quotes(
    @SerializedName("quotes")
    val results: List<Quote>
)