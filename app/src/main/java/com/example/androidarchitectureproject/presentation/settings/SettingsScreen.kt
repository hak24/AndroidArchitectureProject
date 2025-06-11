package com.example.androidarchitectureproject.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sync Enable/Disable Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Auto Sync",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = state.syncEnabled,
                    onCheckedChange = viewModel::toggleSync
                )
            }

            // Sync Interval Selection
            if (state.syncEnabled) {
                Text(
                    text = "Sync Interval",
                    style = MaterialTheme.typography.titleMedium
                )

                Column {
                    SettingsViewModel.SYNC_INTERVALS.forEach { interval ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when {
                                    interval < 60 -> "$interval minutes"
                                    else -> "${interval / 60} hour"
                                }
                            )
                            RadioButton(
                                selected = state.syncInterval == interval,
                                onClick = { viewModel.setSyncInterval(interval) }
                            )
                        }
                    }
                }

                // Info Text
                Text(
                    text = "Images will be automatically synced and cached for offline use at the selected interval when connected to the internet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // About Section
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This app uses the Unsplash API to display beautiful images. " +
                            "You can browse, search, and save your favorite images for offline viewing.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 