package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.data.models.FileState

@Composable
fun FileOperationsCard(
    fileState: FileState,
    onSelectFile: () -> Unit,
    onSendFile: () -> Unit,
    isDeviceSelected: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (fileState.selectedUri != null)
            SelectedFileInfo(fileName = fileState.selectedFileName)

        FileActionButtons(
            fileState = fileState,
            onSelectFile = onSelectFile,
            onSendFile = onSendFile,
            isDeviceSelected = isDeviceSelected
        )
    }
}

@Composable
private fun SelectedFileInfo(fileName: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fileName ?: "Selected file",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.8f)
            )
        }
    }
}

@Composable
private fun FileActionButtons(
    fileState: FileState,
    onSelectFile: () -> Unit,
    onSendFile: () -> Unit,
    isDeviceSelected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SendFileButton(
            onClick = onSendFile,
            fileState = fileState,
            isEnabled = true,
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
private fun SendFileButton(
    onClick: () -> Unit,
    fileState: FileState,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !fileState.isSending,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        if (fileState.isSending) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sending...")
        } else {
            Text("Send File")
        }
    }
}