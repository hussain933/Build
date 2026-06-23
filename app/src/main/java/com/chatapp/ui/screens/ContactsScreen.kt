package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.ChatPreview
import com.chatapp.network.models.User
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    accessToken: String,
    onChatClick: (User) -> Unit,
    onGoHome: () -> Unit,
    onGoProfile: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.getChats("Bearer $accessToken")
            if (res.isSuccessful) chats = res.body()?.data ?: emptyList()
        } catch (_: Exception) {}
        loading = false
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Contacts", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = TextPrimary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
                NavigationBarItem(selected = false, onClick = onGoHome,
                    icon = { Icon(Icons.Default.Home, null) }, label = { Text("Servers") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary))
                NavigationBarItem(selected = true, onClick = {},
                    icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chats") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GreenDark, selectedIconColor = Green, selectedTextColor = Green))
                NavigationBarItem(selected = false, onClick = onGoProfile,
                    icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary))
            }
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Green) }
        } else if (chats.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💬", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Koi chat nahi abhi", color = TextSecondary, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(chats, key = { it.userId }) { chat ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onChatClick(chat.user) }.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(50.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                            Text(chat.user.nickname.take(1).uppercase(), color = Green, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(chat.user.nickname, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                                Text(chat.lastMessage?.createdAt?.take(10) ?: "", fontSize = 11.sp, color = TextSecondary)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                val lastMsg = chat.lastMessage
                                Text(
                                    when {
                                        lastMsg == null -> ""
                                        lastMsg.type == "image" -> "📷 Photo"
                                        lastMsg.type == "voice" -> "🎤 Voice message"
                                        else -> lastMsg.content
                                    },
                                    fontSize = 13.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
                                )
                                if (chat.unreadCount > 0) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(Modifier.size(20.dp).clip(CircleShape).background(Green), contentAlignment = Alignment.Center) {
                                        Text(chat.unreadCount.toString(), color = Background, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    Divider(color = Border.copy(alpha = 0.4f), thickness = 0.5.dp, modifier = Modifier.padding(start = 80.dp))
                }
            }
        }
    }
}
