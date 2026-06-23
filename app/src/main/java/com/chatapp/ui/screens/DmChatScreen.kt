package com.chatapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DmChatScreen(userId: String, onBack: () -> Unit) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Back") }
            Text(text = "Chat", style = MaterialTheme.typography.titleLarge)
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (msg.senderId == "me") Color(0xFF25D366) else Color.DarkGray),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Text(text = msg.content, modifier = Modifier.padding(8.dp), color = Color.White)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            TextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                messages = messages + Message("", input, "me")
                input = ""
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

data class Message(val id: String, val content: String, val senderId: String)
