package com.example.androidarchitectureproject.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidarchitectureproject.BuildConfig
import com.example.androidarchitectureproject.data.local.ImageDao
import com.example.androidarchitectureproject.data.remote.UnsplashApi
import com.example.androidarchitectureproject.domain.model.Image
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class ImagePagingSource(
    private val api: UnsplashApi,
    private val dao: ImageDao
) : PagingSource<Int, Image>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        return try {
            val page = params.key ?: 1
            Timber.d("load called for page: $page")
            
            val localImages = dao.getAllImages().first().map { entity ->
                Image(
                    id = entity.id,
                    description = entity.description ?: "",
                    regularUrl = entity.regularUrl,
                    thumbUrl = entity.thumbUrl,
                    userName = entity.userName,
                    userUsername = entity.userUsername,
                    likes = entity.likes,
                    createdAt = entity.createdAt,
                    isFavorite = entity.isFavorite
                )
            }

            // If we have local data and this is the first page, return it
            if (localImages.isNotEmpty() && page == 1) {
                return LoadResult.Page(
                    data = localImages,
                    prevKey = null,
                    nextKey = 2
                )
            }

            // Otherwise, try to fetch from network
            try {
                val response = api.getPhotos(
                    page = page,
                    clientId = BuildConfig.UNSPLASH_ACCESS_KEY
                )

                // Save to database
                dao.insertImages(response.map { photo ->
                    com.example.androidarchitectureproject.data.local.ImageEntity(
                        id = photo.id,
                        description = photo.description,
                        regularUrl = photo.urls.regular,
                        thumbUrl = photo.urls.thumb,
                        userName = photo.user.name,
                        userUsername = photo.user.username,
                        likes = photo.likes,
                        createdAt = photo.created_at,
                        isFavorite = false
                    )
                })

                // Return network data
                val images = response.map { photo ->
                    Image(
                        id = photo.id,
                        description = photo.description ?: "",
                        regularUrl = photo.urls.regular,
                        thumbUrl = photo.urls.thumb,
                        userName = photo.user.name,
                        userUsername = photo.user.username,
                        likes = photo.likes,
                        createdAt = photo.created_at,
                        isFavorite = false
                    )
                }

                LoadResult.Page(
                    data = images,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (images.isEmpty()) null else page + 1
                )
            } catch (e: Exception) {
                // If network fails and we have local data, return it
                if (localImages.isNotEmpty()) {
                    LoadResult.Page(
                        data = localImages,
                        prevKey = null,
                        nextKey = null // No more pages in offline mode
                    )
                } else {
                    LoadResult.Error(e)
                }
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
} 