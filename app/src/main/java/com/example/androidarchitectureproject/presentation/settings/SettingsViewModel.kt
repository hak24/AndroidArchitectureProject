package com.example.androidarchitectureproject.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.androidarchitectureproject.data.worker.ImageSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val workManager: WorkManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        // In a real app, you would load these from SharedPreferences or DataStore
        _state.update { 
            it.copy(
                syncEnabled = true,
                syncInterval = 15 // Default to 15 minutes
            )
        }
    }

    fun toggleSync(enabled: Boolean) {
        Timber.d("Toggling sync: $enabled")
        _state.update { it.copy(syncEnabled = enabled) }
        if (enabled) {
            scheduleSyncWork()
        } else {
            cancelSyncWork()
        }
    }

    fun setSyncInterval(minutes: Int) {
        Timber.d("Setting sync interval to $minutes minutes")
        _state.update { it.copy(syncInterval = minutes) }
        if (state.value.syncEnabled) {
            scheduleSyncWork()
        }
    }

    private fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Initial immediate sync
        val initialSyncRequest = OneTimeWorkRequestBuilder<ImageSyncWorker>()
            .setConstraints(constraints)
            .build()

        // Periodic sync
        val syncRequest = PeriodicWorkRequestBuilder<ImageSyncWorker>(
            state.value.syncInterval.toLong(),
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.apply {
            // Schedule immediate sync
            enqueueUniqueWork(
                INITIAL_SYNC_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                initialSyncRequest
            )
            Timber.d("Scheduled initial sync")

            // Schedule periodic sync
            enqueueUniquePeriodicWork(
                PERIODIC_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                syncRequest
            )
            Timber.d("Scheduled periodic sync every ${state.value.syncInterval} minutes")
        }
    }

    private fun cancelSyncWork() {
        Timber.d("Cancelling all sync work")
        workManager.apply {
            cancelUniqueWork(INITIAL_SYNC_WORK_NAME)
            cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
        }
    }

    companion object {
        private const val INITIAL_SYNC_WORK_NAME = "initial_sync_work"
        private const val PERIODIC_SYNC_WORK_NAME = "periodic_sync_work"

        // Available sync intervals in minutes
        val SYNC_INTERVALS = listOf(5, 15, 30, 60)
    }
}

data class SettingsState(
    val syncEnabled: Boolean = false,
    val syncInterval: Int = 15 // Default to 15 minutes
) 