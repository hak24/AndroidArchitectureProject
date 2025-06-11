package com.example.androidarchitectureproject.data.repository

import com.example.androidarchitectureproject.BuildConfig
import com.example.androidarchitectureproject.data.local.ImageDao
import com.example.androidarchitectureproject.data.local.ImageEntity
import com.example.androidarchitectureproject.data.remote.UnsplashApi
import com.example.androidarchitectureproject.data.remote.UnsplashPhoto
import com.example.androidarchitectureproject.domain.repository.ImageRepository
import com.example.androidarchitectureproject.domain.model.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val api: UnsplashApi,
    private val dao: ImageDao
) : ImageRepository {

    override suspend fun fetchImages(page: Int) {
        try {
            val response = api.getPhotos(
                page = page,
                clientId = BuildConfig.UNSPLASH_ACCESS_KEY
            )
            val entities = response.map { it.toImageEntity() }
            dao.insertImages(entities)
        } catch (e: Exception) {
            // If network fails, we'll still show cached data
            throw e
        }
    }

    override suspend fun getPhotoById(id: String): Image? {
        return dao.getImageById(id)?.toDomainModel()
    }

    override fun observePhotoById(id: String): Flow<Image?> {
        return dao.observeImageById(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun refreshPhoto(id: String) {
        try {
            val response = api.getPhoto(
                id = id,
                clientId = BuildConfig.UNSPLASH_ACCESS_KEY
            )
            // Preserve favorite status when refreshing
            val existingImage = dao.getImageById(id)
            val entity = response.toImageEntity(isFavorite = existingImage?.isFavorite ?: false)
            dao.insertImage(entity)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getLocalImages(): Flow<List<Image>> {
        return dao.getAllImages().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFavoriteImages(): Flow<List<Image>> {
        return dao.getFavoriteImages().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun toggleFavorite(imageId: String) {
        val image = dao.getImageById(imageId)
        if (image != null) {
            dao.updateImage(image.copy(isFavorite = !image.isFavorite))
        } else {
            try {
                val response = api.getPhoto(
                    id = imageId,
                    clientId = BuildConfig.UNSPLASH_ACCESS_KEY
                )
                val entity = response.toImageEntity(isFavorite = true)
                dao.insertImage(entity)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun getNonDownloadedImages(): List<Image> {
        return dao.getNonDownloadedImages().map { it.toDomainModel() }
    }

    override suspend fun markImageAsDownloaded(imageId: String) {
        dao.markImageAsDownloaded(imageId)
    }

    private fun UnsplashPhoto.toImageEntity(isFavorite: Boolean = false) = ImageEntity(
        id = id,
        description = description ?: "",
        regularUrl = urls.regular,
        thumbUrl = urls.thumb,
        userName = user.name,
        userUsername = user.username,
        likes = likes,
        createdAt = created_at,
        isFavorite = isFavorite,
        isDownloaded = false
    )

    private fun ImageEntity.toDomainModel() = Image(
        id = id,
        description = description,
        regularUrl = regularUrl,
        thumbUrl = thumbUrl,
        userName = userName,
        userUsername = userUsername,
        likes = likes,
        createdAt = createdAt,
        isFavorite = isFavorite
    )
} 