package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.data.models.MessageState
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.viewmodels.MainViewModel


@Composable
fun MessageFileSenderCard(
    viewModel: MainViewModel = viewModel(),
    messageState: MessageState,
    onMessageChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onSendPing: () -> Unit,
    isDeviceSelected: Boolean
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "Unknown file"
            viewModel.selectFile(it, fileName)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            MessageHeader()

            MessageInput(
                messageText = messageState.text,
                onMessageChanged = onMessageChanged,
                isEnabled = isDeviceSelected && !messageState.isSending
            )

            MessageButtons(
                messageState = messageState,
                onSendMessage = onSendMessage,
                onSendPing = onSendPing,
                isDeviceSelected = isDeviceSelected
            )

            FileOperationsCard(
                fileState = uiState.fileState,
                onSelectFile = {
                    filePickerLauncher.launch("*/*")
                },
                onSendFile = {
                    if (uiState.deviceState.selectedDevice == null) {
                        showToast("Please select a device first")
                    } else {
                        viewModel.sendFile(context)
                        showToast("Sending File...")
                    }
                },
                isDeviceSelected = uiState.deviceState.selectedDevice != null
            )
        }
    }
}

@Composable
private fun MessageHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Send Message & File",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun MessageInput(
    messageText: String,
    onMessageChanged: (String) -> Unit,
    isEnabled: Boolean
) {
    TextField(
        value = messageText,
        onValueChange = onMessageChanged,
        label = { Text("Type your message") },
        modifier = Modifier
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
        shape = RoundedCornerShape(8.dp),
        enabled = isEnabled
    )
}

@Composable
private fun MessageButtons(
    messageState: MessageState,
    onSendMessage: () -> Unit,
    onSendPing: () -> Unit,
    isDeviceSelected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PingButton(
            onClick = onSendPing,
            isSending = messageState.isSending,
            isEnabled = isDeviceSelected,
            modifier = Modifier.weight(0.8f)
        )

        SendMessageButton(
            onClick = {
                onSendMessage()
            },
            isSending = messageState.isSending,
            isEnabled = messageState.text.isNotEmpty() && isDeviceSelected,
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
private fun PingButton(
    onClick: () -> Unit,
    isSending: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isSending,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isSending) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sending...")
        } else {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Ping")
        }
    }
}

@Composable
private fun SendMessageButton(
    onClick: () -> Unit,
    isSending: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isSending,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isSending) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sending...")
        } else {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Message")
        }
    }
}