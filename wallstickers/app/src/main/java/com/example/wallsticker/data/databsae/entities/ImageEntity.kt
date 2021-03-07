package com.example.wallsticker.data.databsae.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wallsticker.Model.Images
import com.example.wallsticker.Utilities.Const.Companion.TABLE_IMAGE


@Entity(tableName = TABLE_IMAGE)
class ImageEntity(
    var images: Images
) {

    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
