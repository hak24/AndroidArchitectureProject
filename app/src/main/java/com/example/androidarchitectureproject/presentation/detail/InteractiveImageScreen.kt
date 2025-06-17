package com.example.androidarchitectureproject.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.androidarchitectureproject.util.ImageLoadingUtil
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveImageScreen(
    imageUrl: String,
    description: String?,
    onBackClick: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
                title = { Text("Interactive View", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { rotation = (rotation + 90f) % 360f }) {
                        Icon(
                            imageVector = Icons.Default.Rotate90DegreesCcw,
                            contentDescription = "Rotate image"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageLoadingUtil.createImageRequest(
                    context = LocalContext.current,
                    url = imageUrl,
                    forceDiskCache = false
                ),
                contentDescription = description,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = rotation
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotate ->
                            scale = (scale * zoom).coerceIn(0.5f, 5f)

                            // Apply rotation based on the current rotation state
                            val angleRad = rotation * PI / 180
                            val cos = cos(angleRad).toFloat()
                            val sin = sin(angleRad).toFloat()
                            val rotatedPanX = pan.x * cos - pan.y * sin
                            val rotatedPanY = pan.x * sin + pan.y * cos

                            offset = Offset(
                                x = offset.x + rotatedPanX,
                                y = offset.y + rotatedPanY
                            )
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = if (scale > 1f) 1f else 2f
                                offset = Offset.Zero
                            }
                        )
                    }
            )

            // Reset button when zoomed or rotated
            AnimatedVisibility(
                visible = scale > 1f || rotation != 0f || offset != Offset.Zero,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        scale = 1f
                        rotation = 0f
                        offset = Offset.Zero
                    }
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Default.Undo, contentDescription = "Reset position")
                }
            }
        }
    }
} 