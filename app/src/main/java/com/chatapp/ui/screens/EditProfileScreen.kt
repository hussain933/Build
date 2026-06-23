package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.UpdateProfileRequest
import com.chatapp.network.models.User
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    accessToken: String,
    currentUser: User,
    onBack: () -> Unit,
    onSaved: (User) -> Unit
) {
    val scope = rememberCoroutineScope()
    var nickname by remember { mutableStateOf(currentUser.nickname) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val hasChanged = nickname.trim() != currentUser.nickname

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            containerColor = Surface,
            title = { Text("Unsaved Changes", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Changes discard kar dein?", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { showDiscardDialog = false; onBack() }) { Text("Discard", color = Red, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { showDiscardDialog = false }) { Text("Raho", color = TextSecondary) } }
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { if (hasChanged) showDiscardDialog = true else onBack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp)) {

            // Avatar
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(84.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                    if (currentUser.avatarUrl != null) AsyncImage(currentUser.avatarUrl, null, Modifier.fillMaxSize().clip(CircleShape))
                    else Text(currentUser.nickname.take(1).uppercase(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Green)
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = {}) { Text("Change Photo", color = Green, fontSize = 13.sp) }
            }
            Spacer(Modifier.height(24.dp))

            // Nickname
            Text("Nickname", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = nickname, onValueChange = { nickname = it; errorMsg = "" },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (hasChanged) Green else Border,
                    unfocusedBorderColor = if (hasChanged) Green.copy(alpha = 0.5f) else Border,
                    focusedContainerColor = SurfaceAlt, unfocusedContainerColor = SurfaceAlt,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = Green,
                    focusedLabelColor = Green, unfocusedLabelColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(16.dp))

            // Username (readonly)
            Text("Username", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = "@${currentUser.username}", onValueChange = {},
                singleLine = true, readOnly = true,
                trailingIcon = { Icon(Icons.Default.Lock, null, tint = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Border, unfocusedBorderColor = Border,
                    focusedContainerColor = SurfaceAlt.copy(alpha = 0.5f), unfocusedContainerColor = SurfaceAlt.copy(alpha = 0.5f),
                    focusedTextColor = TextMuted, unfocusedTextColor = TextMuted, cursorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(16.dp))

            // Email (readonly)
            if (currentUser.email != null) {
                Text("Email", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = currentUser.email, onValueChange = {},
                    singleLine = true, readOnly = true,
                    trailingIcon = { Icon(Icons.Default.Lock, null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Border, unfocusedBorderColor = Border,
                        focusedContainerColor = SurfaceAlt.copy(alpha = 0.5f), unfocusedContainerColor = SurfaceAlt.copy(alpha = 0.5f),
                        focusedTextColor = TextMuted, unfocusedTextColor = TextMuted, cursorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            // Password
            Text("Password", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceAlt).padding(horizontal = 14.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("••••••••", color = TextMuted, fontSize = 18.sp, modifier = Modifier.weight(1f), letterSpacing = 3.sp)
                TextButton(onClick = {}) { Text("Change", color = Green, fontSize = 13.sp) }
            }
            Spacer(Modifier.height(28.dp))

            if (errorMsg.isNotEmpty()) { Text(errorMsg, color = Red, fontSize = 13.sp); Spacer(Modifier.height(8.dp)) }

            Button(
                onClick = {
                    if (!hasChanged) return@Button
                    scope.launch {
                        loading = true; errorMsg = ""
                        try {
                            val res = RetrofitClient.api.updateMe("Bearer $accessToken", UpdateProfileRequest(nickname = nickname.trim()))
                            if (res.isSuccessful && res.body()?.data != null) onSaved(res.body()!!.data!!)
                            else errorMsg = res.body()?.error?.message ?: "Update fail hua"
                        } catch (e: Exception) { errorMsg = "Network error: ${e.message}" }
                        loading = false
                    }
                },
                enabled = hasChanged && !loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background, disabledContainerColor = SurfaceAlt, disabledContentColor = TextMuted)
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(22.dp), color = Background, strokeWidth = 2.dp)
                else Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
