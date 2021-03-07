package com.example.wallsticker.data.databsae.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wallsticker.Model.Quotes
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.Utilities.Const.Companion.TABLE_QUOTE


@Entity(tableName = TABLE_QUOTE)
class QuoteEntity(
    var quotes: Quotes
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}