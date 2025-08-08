package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReceivedMessagesCard(
    receivedMessages: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MessagesHeader(messageCount = receivedMessages.size)
            MessagesContent(messages = receivedMessages)
        }
    }
}

@Composable
private fun MessagesHeader(messageCount: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Received Messages & Files ($messageCount)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

    }
}

@Composable
private fun MessagesContent(messages: List<String>) {
    if (messages.isEmpty()) {
        EmptyMessagesState()
    } else {
        MessagesList(messages = messages)
    }
}

@Composable
private fun EmptyMessagesState() {
    Text(
        text = "No messages received yet",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun MessagesList(messages: List<String>) {
    messages.forEach { message ->
        MessageItem(message = message)
    }
}

@Composable
private fun MessageItem(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}