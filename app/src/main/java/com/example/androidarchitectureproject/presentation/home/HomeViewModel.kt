package com.example.androidarchitectureproject.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.androidarchitectureproject.data.local.ImageDao
import com.example.androidarchitectureproject.data.paging.ImagePagingSource
import com.example.androidarchitectureproject.data.remote.UnsplashApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: UnsplashApi,
    private val dao: ImageDao
) : ViewModel() {

    val pagingDataFlow = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = 3
        ),
        pagingSourceFactory = { ImagePagingSource(api, dao) }
    ).flow.cachedIn(viewModelScope)

    companion object {
        private const val PAGE_SIZE = 20
    }
} 