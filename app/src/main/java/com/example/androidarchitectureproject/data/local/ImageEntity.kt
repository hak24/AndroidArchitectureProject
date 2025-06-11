package com.example.androidarchitectureproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey
    val id: String,
    val regularUrl: String,
    val thumbUrl: String,
    val description: String?,
    val userName: String,
    val userUsername: String,
    val likes: Int,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val createdAt: String
) 