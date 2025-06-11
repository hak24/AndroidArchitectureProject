package com.example.androidarchitectureproject.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidarchitectureproject.data.local.ImageDao
import com.example.androidarchitectureproject.data.paging.ImagePagingSource
import com.example.androidarchitectureproject.data.remote.UnsplashApi
import com.example.androidarchitectureproject.domain.model.Image
import com.example.androidarchitectureproject.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: UnsplashApi,
    private val dao: ImageDao,
    private val repository: ImageRepository
) : ViewModel() {

    val pagingDataFlow = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = 3
        ),
        pagingSourceFactory = { ImagePagingSource(api, dao) }
    ).flow.cachedIn(viewModelScope)

    init {
        // Initial fetch to ensure we have some data
        viewModelScope.launch {
            try {
                repository.fetchImages(1)
            } catch (e: Exception) {
                // Ignore error as we'll show cached data anyway
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
} 