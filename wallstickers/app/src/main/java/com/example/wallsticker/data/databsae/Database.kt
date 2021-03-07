package com.example.wallsticker.data.databsae

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wallsticker.data.databsae.entities.CategoryEntity
import com.example.wallsticker.data.databsae.entities.FavoritesEntity
import com.example.wallsticker.data.databsae.entities.ImageEntity


@Database(
    entities = [ImageEntity::class, FavoritesEntity::class,CategoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(imageTypeConverter::class)
abstract class Database :RoomDatabase(){

    abstract fun imageDao(): ImageDao
}