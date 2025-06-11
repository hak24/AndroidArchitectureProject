package com.example.androidarchitectureproject.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidarchitectureproject.domain.model.Image
import com.example.androidarchitectureproject.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val repository: ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val imageId: String = checkNotNull(savedStateHandle["imageId"])

    private val _state = MutableStateFlow(ImageDetailState())
    val state: StateFlow<ImageDetailState> = _state.asStateFlow()

    init {
        observeImage()
        refreshImage()
    }

    private fun observeImage() {
        viewModelScope.launch {
            repository.observePhotoById(imageId)
                .onStart { _state.value = _state.value.copy(isLoading = true) }
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load image"
                    )
                }
                .collect { image ->
                    _state.value = _state.value.copy(
                        image = image,
                        isLoading = false,
                        error = if (image == null) "Image not found" else null
                    )
                }
        }
    }

    private fun refreshImage() {
        viewModelScope.launch {
            try {
                repository.refreshPhoto(imageId)
            } catch (e: Exception) {
                // If refresh fails, we still have local data from observeImage
                if (_state.value.image == null) {
                    _state.value = _state.value.copy(
                        error = e.message ?: "Failed to refresh image"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(imageId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to update favorite status"
                )
            }
        }
    }
}

data class ImageDetailState(
    val image: Image? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) 