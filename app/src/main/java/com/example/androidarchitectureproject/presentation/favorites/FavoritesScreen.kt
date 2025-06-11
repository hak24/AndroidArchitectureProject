package com.example.androidarchitectureproject.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.androidarchitectureproject.domain.model.Image

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onImageClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.images.isEmpty()) {
            Text(
                text = "No favorite images yet",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            ImageGrid(
                images = state.images,
                onImageClick = onImageClick
            )
        }
    }
}

@Composable
private fun ImageGrid(
    images: List<Image>,
    onImageClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(images) { image ->
            ImageCard(
                image = image,
                onImageClick = onImageClick,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun ImageCard(
    image: Image,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onImageClick(image.id) }
    ) {
        AsyncImage(
            model = image.thumbUrl,
            contentDescription = image.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
} 