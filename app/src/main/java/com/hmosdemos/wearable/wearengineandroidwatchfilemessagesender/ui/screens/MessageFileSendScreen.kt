package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.viewmodels.MainViewModel
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.screens.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageFileSenderScreen(
    viewModel: MainViewModel = viewModel(),
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var toastMessage by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "Unknown file"
            viewModel.selectFile(it, fileName)
        }
    }

    LaunchedEffect(key1 = toastMessage) {
        if (toastMessage.isNotBlank()) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            toastMessage = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        item {
            SelectDeviceCard(
                deviceState = uiState.deviceState,
                onDeviceSelected = { device ->
                    viewModel.selectDevice(device)
                    toastMessage = "Selected: ${device.name}"
                },
                onRefreshClicked = {
                    viewModel.refreshDevices()
                    toastMessage = "Refreshing devices..."
                }
            )
        }

        item {
            MessageFileSenderCard(
                viewModel = viewModel,
                messageState = uiState.messageState,
                onMessageChanged = viewModel::updateMessageText,
                onSendMessage = {
                    when {
                        uiState.deviceState.selectedDevice == null -> {
                            toastMessage = "Please select a device first"
                        }

                        uiState.messageState.text.trim().isEmpty() -> {
                            toastMessage = "Message cannot be empty"
                        }

                        else -> {
                            toastMessage = "Sending message..."
                            viewModel.sendMessage(uiState.messageState.text)
                        }
                    }
                },
                onSendPing = {
                    if (uiState.deviceState.selectedDevice == null) {
                        toastMessage = "Please select a device first"
                    } else {
                        toastMessage = "Sending ping..."
                        viewModel.sendPing()
                    }
                },
                isDeviceSelected = uiState.deviceState.selectedDevice != null
            )
        }

        item {
            ReceivedMessagesCard(
                receivedMessages = uiState.receivedMessages
            )
        }

        item {
            LogCard(
                onClearLogs = {
                    viewModel.clearLogs()
                },
                logMessages = uiState.logMessages
            )
        }
    }

}