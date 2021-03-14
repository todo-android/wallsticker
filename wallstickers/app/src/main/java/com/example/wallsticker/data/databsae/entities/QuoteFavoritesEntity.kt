package com.example.wallsticker.data.databsae.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wallsticker.Model.Quote
import com.example.wallsticker.Utilities.Const.Companion.TABLE_QUOTE_FAVORITE


@Entity(tableName = TABLE_QUOTE_FAVORITE)
class QuoteFavoritesEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var quote: Quote
)