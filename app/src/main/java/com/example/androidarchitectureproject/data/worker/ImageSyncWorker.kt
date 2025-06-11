package com.example.androidarchitectureproject.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import com.example.androidarchitectureproject.di.IODispatcherAnnotation
import com.example.androidarchitectureproject.domain.repository.ImageRepository
import com.example.androidarchitectureproject.util.ImageLoadingUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class ImageSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: ImageRepository,
    private val imageLoader: ImageLoader,
    @IODispatcherAnnotation private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            Timber.d("Starting image sync work")
            repository.fetchImages(1)
            Timber.d("Fetched new images from API")

            // Get non-downloaded images
            val nonDownloadedImages = repository.getNonDownloadedImages()
            Timber.d("Found ${nonDownloadedImages.size} non-downloaded images")

            nonDownloadedImages.forEach { image ->
                try {
                    Timber.d("Caching full resolution image: ${image.id}")
                    val request = ImageLoadingUtil.createImageRequest(
                        context = applicationContext,
                        url = image.regularUrl,
                        forceDiskCache = true
                    )

                    imageLoader.execute(request)
                    // Mark image as downloaded after successful caching
                    repository.markImageAsDownloaded(image.id)
                    Timber.d("Successfully cached and marked as downloaded: ${image.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to cache image: ${image.id}")
                }
            }
            
            Timber.d("Image sync work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Image sync work failed")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "image_sync_worker"
    }
} 