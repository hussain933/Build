package com.chatapp.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.AddMemberRequest
import com.chatapp.network.models.User
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMembersScreen(
    accessToken: String,
    serverId: String,
    inviteCode: String,
    memberUserIds: List<String>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<User>>(emptyList()) }
    var addedIds by remember { mutableStateOf(memberUserIds.toSet()) }
    var searchLoading by remember { mutableStateOf(false) }

    val inviteLink = "https://chatapp.invite/$inviteCode"

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            searchLoading = true
            try {
                val res = RetrofitClient.api.searchUsers("Bearer $accessToken", searchQuery)
                if (res.isSuccessful) {
                    searchResults = (res.body()?.data ?: emptyList()).filter { it.id !in addedIds }
                }
            } catch (_: Exception) {}
            searchLoading = false
        } else { searchResults = emptyList() }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Add Members", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Invite card
            item {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Surface).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Link, null, tint = Green, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Invite to Server", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(GreenMuted).padding(12.dp)) {
                        Text(inviteLink, color = Green, fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("invite", inviteLink))
                                Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Green)
                        ) { Icon(Icons.Default.ContentCopy, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Copy Link", fontSize = 13.sp) }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "Server join karo: $inviteLink")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share via"))
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                        ) { Icon(Icons.Default.Share, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Share", fontSize = 13.sp) }
                    }
                }
            }

            // OR divider
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(Modifier.weight(1f), color = Border)
                    Text("  OR  ", color = TextMuted, fontSize = 12.sp)
                    Divider(Modifier.weight(1f), color = Border)
                }
            }

            // Search
            item {
                Text("Search Users", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = searchQuery, onValueChange = { searchQuery = it },
                    placeholder = { Text("Search username...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (searchLoading) {
                item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Green, modifier = Modifier.size(24.dp)) } }
            }

            items(searchResults, key = { it.id }) { user ->
                val isAdded = user.id in addedIds
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                        Text(user.nickname.take(1).uppercase(), color = Green, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(user.nickname, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                        Text("@${user.username}", color = TextSecondary, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            if (!isAdded) scope.launch {
                                try {
                                    val res = RetrofitClient.api.addMember("Bearer $accessToken", serverId, AddMemberRequest(user.id))
                                    if (res.isSuccessful) addedIds = addedIds + user.id
                                } catch (_: Exception) {}
                            }
                        },
                        enabled = !isAdded,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdded) SurfaceAlt else Green,
                            contentColor = if (isAdded) TextMuted else Background,
                            disabledContainerColor = SurfaceAlt,
                            disabledContentColor = TextMuted
                        )
                    ) { Text(if (isAdded) "Added ✓" else "Add", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}
