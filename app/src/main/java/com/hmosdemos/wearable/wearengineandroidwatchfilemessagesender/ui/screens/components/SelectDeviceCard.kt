package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huawei.wearengine.device.Device
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.data.models.DeviceState

@Composable
fun SelectDeviceCard(
    deviceState: DeviceState,
    onDeviceSelected: (Device) -> Unit,
    onRefreshClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Device",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                ) {
                    if (deviceState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(4.dp),
                            strokeWidth = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    IconButton(
                        onClick = onRefreshClicked,
                        enabled = !deviceState.isLoading
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh Devices",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }


            Text(
                text = if (deviceState.permissionsGranted) {
                    "Choose a bonded device"
                } else {
                    "Permissions required"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            if (deviceState.devices.isEmpty() && !deviceState.isLoading) {
                Text(
                    text = if (deviceState.permissionsGranted) {
                        "No devices found"
                    } else {
                        "Need permissions"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    items(deviceState.devices.size) { index ->
                        DeviceItem(
                            device = deviceState.devices[index],
                            isSelected = deviceState.selectedDevice == deviceState.devices[index],
                            onDeviceSelected = onDeviceSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceItem(
    device: Device,
    isSelected: Boolean,
    onDeviceSelected: (Device) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceSelected(device) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onDeviceSelected(device) }
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = device.name ?: "Unknown Device",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )
    }
}
