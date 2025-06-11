package com.example.androidarchitectureproject.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.androidarchitectureproject.domain.model.Image
import com.example.androidarchitectureproject.util.ImageLoadingUtil
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onImageClick: (String) -> Unit,
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            windowInsets = WindowInsets(
                top = 0.dp,
                bottom = 0.dp
            ),
            title = { Text("Image Splasher", textAlign = TextAlign.Center) },
        )
    }) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            when (lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is LoadState.Error -> {
                    val error = (lazyPagingItems.loadState.refresh as LoadState.Error).error
                    Text(
                        text = error.localizedMessage ?: "An error occurred",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    ImageGrid(
                        pagingItems = lazyPagingItems,
                        onImageClick = onImageClick
                    )
                }
            }

            if (lazyPagingItems.loadState.append is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ImageGrid(
    pagingItems: androidx.paging.compose.LazyPagingItems<Image>,
    onImageClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                val item = pagingItems[index]
                if (item != null) {
                    "${item.id}_$index"
                } else {
                    "null_$index"
                }
            }
        ) { index ->
            pagingItems[index]?.let { image ->
                ImageCard(
                    image = image,
                    onImageClick = onImageClick,
                    modifier = Modifier.padding(4.dp)
                )
            }
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
            model = ImageLoadingUtil.createImageRequest(
                context = LocalContext.current,
                url = image.thumbUrl,
                forceDiskCache = true
            ),
            contentDescription = image.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
} 