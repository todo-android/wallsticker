package com.example.wallsticker.data.databsae

import androidx.room.*
import com.example.wallsticker.data.databsae.entities.CategoryEntity
import com.example.wallsticker.data.databsae.entities.FavoritesEntity
import com.example.wallsticker.data.databsae.entities.ImageEntity
import com.example.wallsticker.data.databsae.entities.QuoteEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quoteEntity: QuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM tbl_quote")
    fun readQuotes(): Flow<List<QuoteEntity>>

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
}