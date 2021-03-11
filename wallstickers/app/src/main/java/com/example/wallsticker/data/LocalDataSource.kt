package com.example.wallsticker.data

import com.example.wallsticker.data.databsae.ImageDao
import com.example.wallsticker.data.databsae.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val WallDao: ImageDao,

    ) {


    /**fro images **/
    fun readdatabase(): Flow<List<ImageEntity>> {
        return WallDao.readImages()
    }

    suspend fun insertImage(imageEntity: ImageEntity) {
        WallDao.insertImage(imageEntity)
    }

    fun readCategories(): Flow<List<CategoryEntity>> {
        return WallDao.readCategories()
    }

    suspend fun insertCategories(categoryEntity: CategoryEntity) {
        WallDao.insertCategories(categoryEntity)
    }

    fun readFavorite(): Flow<List<FavoritesEntity>> {
        return WallDao.readFavoriteImages()
    }

    suspend fun insertFavorite(favoritesEntity: FavoritesEntity) {
        WallDao.insertFavoriteImage(favoritesEntity)
    }

    suspend fun deleteFavorite(favoritesEntity: FavoritesEntity) {
        WallDao.deleteFavoriteImage(favoritesEntity)
    }

    fun readImageByCategory(): Flow<List<ImageEntity>> {
        return WallDao.readImagesByCategory()
    }

    /**fro quotes**/

    suspend fun insertQuotes(quoteEntity: QuoteEntity) {
        WallDao.insertQuotes(quoteEntity)
    }

    fun readQuotes(): Flow<List<QuoteEntity>> {
        return WallDao.readQuotes()
    }

    suspend fun insertCategoriesQuotes(categoryEntity: QuotesCategoryEntity){
        WallDao.insertCategoriesQuotes(categoryEntity)
    }

    fun readCategoriesQuotes():Flow<List<QuotesCategoryEntity>>{
        return WallDao.readCategoriesQuotes()
    }


}