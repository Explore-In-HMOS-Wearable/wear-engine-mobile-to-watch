package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.screens.MessageFileSenderScreen
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearEngineApp(
    viewModel: MainViewModel,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Wear Engine Message & File Sender",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = {
                            viewModel.clearLogs()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Logs",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        MessageFileSenderScreen(
            viewModel = viewModel,
            contentPadding = contentPadding
        )
    }
}