package com.example.androidarchitectureproject.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM images ORDER BY createdAt DESC")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE isDownloaded = 0")
    suspend fun getNonDownloadedImages(): List<ImageEntity>

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getImageById(id: String): ImageEntity?

    @Query("SELECT * FROM images WHERE id = :id")
    fun observeImageById(id: String): Flow<ImageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Update
    suspend fun updateImage(image: ImageEntity)

    @Query("UPDATE images SET isDownloaded = 1 WHERE id = :imageId")
    suspend fun markImageAsDownloaded(imageId: String)

    @Query("DELETE FROM images WHERE id = :imageId")
    suspend fun deleteImage(imageId: String)
} 