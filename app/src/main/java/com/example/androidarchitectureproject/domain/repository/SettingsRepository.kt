package com.example.androidarchitectureproject.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Settings>
    suspend fun updateSyncEnabled(enabled: Boolean)
    suspend fun updateSyncInterval(minutes: Int)
}

data class Settings(
    val syncEnabled: Boolean = false,
    val syncInterval: Int = 15
) 