package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.CreateServerRequest
import com.chatapp.network.models.Server
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServerScreen(accessToken: String, onBack: () -> Unit, onCreated: (Server) -> Unit) {
    val scope = rememberCoroutineScope()
    var serverName by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Create Server", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(20.dp))
            Box(Modifier.size(90.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AddPhotoAlternate, null, tint = Green, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text("Upload Server Icon", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(32.dp))
            Text("Server Name", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = serverName, onValueChange = { serverName = it; errorMsg = "" },
                singleLine = true, placeholder = { Text("Server ka naam", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedFieldColors(), shape = RoundedCornerShape(12.dp)
            )
            if (errorMsg.isNotEmpty()) { Spacer(Modifier.height(10.dp)); Text(errorMsg, color = Red, fontSize = 13.sp) }
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    if (serverName.isBlank()) { errorMsg = "Server ka naam likho"; return@Button }
                    scope.launch {
                        loading = true; errorMsg = ""
                        try {
                            val res = RetrofitClient.api.createServer("Bearer $accessToken", CreateServerRequest(serverName.trim()))
                            if (res.isSuccessful && res.body()?.data != null) onCreated(res.body()!!.data!!)
                            else errorMsg = res.body()?.error?.message ?: "Server nahi bana"
                        } catch (e: Exception) { errorMsg = "Network error" }
                        loading = false
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(22.dp), color = Background, strokeWidth = 2.dp)
                else Text("Create Server", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
