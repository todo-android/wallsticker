package com.example.wallsticker.data.databsae

import androidx.room.*
import com.example.wallsticker.data.databsae.entities.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageEntity: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM tbl_images")
    fun readImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM tbl_category ORDER BY id ASC")
    fun readCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteImage(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorite_tbl ORDER BY id ASC")
    fun readFavoriteImages(): Flow<List<FavoritesEntity>>

    @Delete
    suspend fun deleteFavoriteImage(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM tbl_images")
    fun readImagesByCategory(): Flow<List<ImageEntity>>


    /**for quotes ----------------------------------------------------------------------**/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quoteEntity: QuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoriesQuotes(quotesCategoryEntity: QuotesCategoryEntity)

    @Query("SELECT * FROM tbl_quote")
    fun readQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM tbl_category_quote")
    fun readCategoriesQuotes(): Flow<List<QuotesCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteQuotes(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorite_tbl ORDER BY id ASC")
    fun readFavoriteQuotes(): Flow<List<FavoritesEntity>>

    @Delete
    suspend fun deleteFavoriteQuotes(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM tbl_images")
    fun readQuotesByCategory(): Flow<List<ImageEntity>>



}