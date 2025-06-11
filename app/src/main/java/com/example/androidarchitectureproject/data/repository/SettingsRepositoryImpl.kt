package com.example.androidarchitectureproject.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidarchitectureproject.domain.repository.Settings
import com.example.androidarchitectureproject.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    context: Context
) : SettingsRepository {
    private val dataStore = context.settingsDataStore

    override fun getSettings(): Flow<Settings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Settings(
                syncEnabled = preferences[PreferencesKeys.SYNC_ENABLED] ?: false,
                syncInterval = preferences[PreferencesKeys.SYNC_INTERVAL] ?: 15
            )
        }

    override suspend fun updateSyncEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SYNC_ENABLED] = enabled
        }
    }

    override suspend fun updateSyncInterval(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SYNC_INTERVAL] = minutes
        }
    }

    private object PreferencesKeys {
        val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")
        val SYNC_INTERVAL = intPreferencesKey("sync_interval")
    }
} 