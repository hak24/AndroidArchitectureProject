package com.example.androidarchitectureproject.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.androidarchitectureproject.di.IODispatcherAnnotation
import com.example.androidarchitectureproject.domain.repository.ImageRepository
import com.example.androidarchitectureproject.util.ImageLoadingUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class ImageSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: ImageRepository,
    private val imageLoader: ImageLoader,
    @IODispatcherAnnotation private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, workerParams), ImageRequest.Listener {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {

            // Get non-downloaded images
            val nonDownloadedImages = repository.getNonDownloadedImages()
            Timber.d("Found ${nonDownloadedImages.size} non-downloaded images")

            nonDownloadedImages.forEach { image ->
                try {
                    Timber.d("Caching full resolution image: ${image.id}")
                    val requestRegularUrl = ImageLoadingUtil.createImageRequest(
                        context = applicationContext,
                        url = image.regularUrl,
                        forceDiskCache = true,
                        listener = this@ImageSyncWorker
                    )

                    imageLoader.execute(requestRegularUrl)
                    val requestThumbUrl = ImageLoadingUtil.createImageRequest(
                        context = applicationContext,
                        url = image.thumbUrl,
                        forceDiskCache = true,
                        listener = this@ImageSyncWorker
                    )

                    imageLoader.execute(requestThumbUrl)
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