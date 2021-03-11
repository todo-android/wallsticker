package com.example.wallsticker.data.databsae.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Utilities.Const.Companion.TABLE_CATEGORY
import com.example.wallsticker.Utilities.Const.Companion.TABLE_CATEGOY_QUOTE


@Entity(tableName = TABLE_CATEGOY_QUOTE)
class QuotesCategoryEntity(

    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var Categories: Categories
)