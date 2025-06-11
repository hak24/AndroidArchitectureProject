package com.example.androidarchitectureproject.domain.model

data class Image(
    val id: String,
    val description: String?,
    val regularUrl: String,
    val thumbUrl: String,
    val userName: String,
    val userUsername: String,
    val likes: Int,
    val createdAt: String,
    val isFavorite: Boolean
) 