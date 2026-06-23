package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.Message
import com.chatapp.network.models.SendMessageRequest
import com.chatapp.network.models.Server
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerChatScreen(
    accessToken: String,
    myUserId: String,
    server: Server,
    onBack: () -> Unit,
    onAddMembers: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.getMessages("Bearer $accessToken", server.id)
            if (res.isSuccessful) messages = (res.body()?.data ?: emptyList()).reversed()
        } catch (_: Exception) {}
        loading = false
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                            if (server.iconUrl != null) AsyncImage(server.iconUrl, null, Modifier.fillMaxSize())
                            else Text(server.name.take(1).uppercase(), color = Green, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(server.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${server.members?.size ?: 0} members", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                actions = {
                    IconButton(onClick = onAddMembers) { Icon(Icons.Default.PersonAdd, null, tint = TextPrimary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().background(Surface).padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.AttachFile, null, tint = TextSecondary) }
                OutlinedTextField(
                    value = inputText, onValueChange = { inputText = it },
                    placeholder = { Text("Type...", color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Border, unfocusedBorderColor = Border,
                        focusedContainerColor = SurfaceAlt, unfocusedContainerColor = SurfaceAlt,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = Green
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )
                Spacer(Modifier.width(8.dp))
                if (inputText.isBlank()) {
                    IconButton(onClick = {}) { Icon(Icons.Default.Mic, null, tint = Green) }
                } else {
                    IconButton(
                        onClick = {
                            val text = inputText.trim(); if (text.isEmpty()) return@IconButton
                            inputText = ""
                            scope.launch {
                                try {
                                    val res = RetrofitClient.api.sendMessage("Bearer $accessToken", server.id, SendMessageRequest(text))
                                    if (res.isSuccessful) res.body()?.data?.let { messages = messages + it }
                                } catch (_: Exception) {}
                            }
                        }
                    ) { Icon(Icons.Default.Send, null, tint = Green) }
                }
            }
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Green)
            }
        } else {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == myUserId
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start) {
                        if (!isMe) {
                            Box(Modifier.size(32.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                                Text(msg.sender?.nickname?.take(1)?.uppercase() ?: "?", color = Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                            if (!isMe) Text(msg.sender?.nickname ?: "User", color = Green, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Box(
                                Modifier.clip(RoundedCornerShape(16.dp)).background(if (isMe) BubbleMine else Bubble).padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                if (msg.type == "text") {
                                    Text(msg.content, color = TextPrimary, fontSize = 14.sp)
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Mic, null, tint = Green, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Voice message", color = TextPrimary, fontSize = 13.sp)
                                    }
                                }
                            }
                            Text(msg.createdAt.take(16).replace("T", " "), color = TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp, horizontal = 4.dp))
                        }
                        if (isMe) {
                            Spacer(Modifier.width(8.dp))
                            Box(Modifier.size(32.dp).clip(CircleShape).background(Green), contentAlignment = Alignment.Center) {
                                Text("You", color = Background, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
