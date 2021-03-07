package com.example.wallsticker.data

import com.example.wallsticker.data.databsae.ImageDao
import com.example.wallsticker.data.databsae.QuoteDao
import com.example.wallsticker.data.databsae.entities.CategoryEntity
import com.example.wallsticker.data.databsae.entities.FavoritesEntity
import com.example.wallsticker.data.databsae.entities.ImageEntity
import com.example.wallsticker.data.databsae.entities.QuoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val imageDao: ImageDao,
    private val quoteDao: QuoteDao
) {


    /**fro images **/
     fun readdatabase(): Flow<List<ImageEntity>>{
        return imageDao.readImages()
    }

    suspend fun insertImage(imageEntity: ImageEntity){
        imageDao.insertImage(imageEntity)
    }
    fun readCategories():Flow<List<CategoryEntity>>{
        return imageDao.readCategories()
    }

    suspend fun insertCategories(categoryEntity: CategoryEntity){
        imageDao.insertCategories(categoryEntity)
    }

    fun readFavorite(): Flow<List<FavoritesEntity>> {
        return imageDao.readFavoriteImages()
    }

    suspend fun insertFavorite(favoritesEntity: FavoritesEntity) {
        imageDao.insertFavoriteImage(favoritesEntity)
    }

    suspend fun deleteFavorite(favoritesEntity: FavoritesEntity) {
        imageDao.deleteFavoriteImage(favoritesEntity)
    }

    fun readImageByCategory():Flow<List<ImageEntity>>{
        return imageDao.readImagesByCategory()
    }

    /**fro quotes**/

    suspend fun insertQuotes(quoteEntity: QuoteEntity){
        quoteDao.insertQuotes(quoteEntity)
    }

    fun readQuotes():Flow<List<QuoteEntity>>{
        return quoteDao.readQuotes()
    }

}