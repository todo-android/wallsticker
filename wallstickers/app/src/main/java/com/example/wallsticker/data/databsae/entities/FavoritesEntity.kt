package com.example.wallsticker.data.databsae.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wallsticker.Model.Image
import com.example.wallsticker.Utilities.Const.Companion.TABLE_FAVORITE


@Entity(tableName = TABLE_FAVORITE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var image: Image
)