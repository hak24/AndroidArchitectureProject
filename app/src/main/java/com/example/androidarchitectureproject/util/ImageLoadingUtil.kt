package com.example.androidarchitectureproject.util

import android.content.Context
import coil.request.CachePolicy
import coil.request.ImageRequest

object ImageLoadingUtil {
    fun createImageRequest(
        context: Context,
        url: String,
        forceDiskCache: Boolean = false
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(url)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(if (forceDiskCache) CachePolicy.DISABLED else CachePolicy.ENABLED)
            .crossfade(true)
            .build()
    }
} 