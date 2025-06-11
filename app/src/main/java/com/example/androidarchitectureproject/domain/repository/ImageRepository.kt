package com.example.androidarchitectureproject.domain.repository

import com.example.androidarchitectureproject.domain.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun fetchImages(page: Int)
    suspend fun getPhotoById(id: String): Image?
    suspend fun refreshPhoto(id: String)
    fun observePhotoById(id: String): Flow<Image?>
    fun getLocalImages(): Flow<List<Image>>
    fun getFavoriteImages(): Flow<List<Image>>
    suspend fun toggleFavorite(imageId: String)
    suspend fun getNonDownloadedImages(): List<Image>
    suspend fun markImageAsDownloaded(imageId: String)
} 