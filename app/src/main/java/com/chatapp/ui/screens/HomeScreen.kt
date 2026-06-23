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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.Server
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    accessToken: String,
    onServerClick: (Server) -> Unit,
    onGoContacts: () -> Unit,
    onGoProfile: () -> Unit,
    onCreateServer: () -> Unit,
    onJoinServer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var servers by remember { mutableStateOf<List<Server>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.getServers("Bearer $accessToken")
            if (res.isSuccessful) servers = res.body()?.data ?: emptyList()
        } catch (_: Exception) {}
        loading = false
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, null) }, label = { Text("Servers") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GreenDark, selectedIconColor = Green, selectedTextColor = Green, unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
                )
                NavigationBarItem(
                    selected = selectedTab == 1, onClick = { selectedTab = 1; onGoContacts() },
                    icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chats") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GreenDark, selectedIconColor = Green, selectedTextColor = Green, unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
                )
                NavigationBarItem(
                    selected = selectedTab == 2, onClick = { selectedTab = 2; onGoProfile() },
                    icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GreenDark, selectedIconColor = Green, selectedTextColor = Green, unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
                )
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Top bar
            Row(Modifier.fillMaxWidth().background(Surface).padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("💬 ChatApp", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
            }

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Green)
                }
            } else if (servers.isEmpty()) {
                // Empty state
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("🌐", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Abhi koi server nahi", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Server banao ya join karo", fontSize = 14.sp, color = TextSecondary)
                    Spacer(Modifier.height(28.dp))
                    Button(onClick = onCreateServer, modifier = Modifier.fillMaxWidth(0.65f).height(50.dp), shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)) {
                        Text("Create Server", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onJoinServer, modifier = Modifier.fillMaxWidth(0.65f).height(50.dp), shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Green)) {
                        Text("Join Server", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Server list
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Servers", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Row {
                                TextButton(onClick = onJoinServer) { Text("Join", color = Green, fontSize = 13.sp) }
                                TextButton(onClick = onCreateServer) { Text("+ Banao", color = Green, fontSize = 13.sp) }
                            }
                        }
                    }
                    items(servers) { server ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface)
                                .clickable { onServerClick(server) }.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(48.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                                if (server.iconUrl != null) AsyncImage(server.iconUrl, null, Modifier.fillMaxSize())
                                else Text(server.name.take(1).uppercase(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Green)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(server.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                                Text("${server.members?.size ?: 0} members", fontSize = 12.sp, color = TextSecondary)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
