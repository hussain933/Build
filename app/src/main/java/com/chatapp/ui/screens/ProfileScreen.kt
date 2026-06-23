package com.chatapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile Screen", style = MaterialTheme.typography.headlineMedium)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Icon(Icons.Filled.Home, contentDescription = "Home")
            Icon(Icons.Filled.Chat, contentDescription = "Chat")
            Icon(Icons.Filled.Person, contentDescription = "Person")
        }
    }
}
