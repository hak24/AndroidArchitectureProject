package com.example.androidarchitectureproject.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidarchitectureproject.domain.model.Image
import com.example.androidarchitectureproject.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavoriteImages().collect { images ->
                _state.update { it.copy(images = images) }
            }
        }
    }

    fun toggleFavorite(imageId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(imageId)
        }
    }
}

data class FavoritesState(
    val images: List<Image> = emptyList()
) 