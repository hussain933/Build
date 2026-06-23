package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.RetrofitClient
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(accessToken: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var gmail by remember { mutableStateOf("") }
    var appPassword by remember { mutableStateOf("") }
    var smtpPort by remember { mutableStateOf("587") }
    var passwordVisible by remember { mutableStateOf(false) }
    var useFirebase by remember { mutableStateOf(false) }
    var firebaseApiKey by remember { mutableStateOf("") }
    var firebaseProjectId by remember { mutableStateOf("") }
    var firebaseAuthDomain by remember { mutableStateOf("") }
    var otpTemplate by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var stats by remember { mutableStateOf<com.chatapp.network.models.AdminStats?>(null) }

    LaunchedEffect(Unit) {
        try {
            val sRes = RetrofitClient.api.getAdminSettings("Bearer $accessToken")
            sRes.body()?.data?.let { s ->
                gmail = s.gmailUser ?: ""; smtpPort = s.smtpPort?.toString() ?: "587"
                otpTemplate = s.otpTemplate ?: ""
            }
            val stRes = RetrofitClient.api.getAdminStats("Bearer $accessToken")
            stats = stRes.body()?.data
        } catch (_: Exception) {}
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, null, tint = Green, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Admin Settings", fontWeight = FontWeight.Bold, color = TextPrimary)
                }},
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Stats
            if (stats != null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        "👥" to "${stats!!.totalUsers}\nUsers",
                        "🌐" to "${stats!!.totalServers}\nServers",
                        "💬" to "${stats!!.totalMessages}\nMsgs",
                        "🟢" to "${stats!!.onlineUsers}\nOnline"
                    ).forEach { (icon, label) ->
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Surface).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(icon, fontSize = 20.sp)
                            Text(label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Email Settings
            AdminSection("Email Settings") {
                AdminField("Gmail", gmail, { gmail = it }, false)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = appPassword, onValueChange = { appPassword = it },
                    label = { Text("App Password") }, singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = TextSecondary)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(), colors = outlinedFieldColors(), shape = RoundedCornerShape(10.dp)
                )
                Spacer(Modifier.height(10.dp))
                AdminField("SMTP Port", smtpPort, { smtpPort = it }, false)
            }

            // OTP Template
            AdminSection("OTP Email Template") {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(SurfaceAlt).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("HTML Template Edit Karo →", color = Green, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.Code, null, tint = Green)
                }
            }

            // Database toggle
            AdminSection("Database") {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(SurfaceAlt).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Firebase", color = if (useFirebase) Green else TextSecondary, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = useFirebase, onCheckedChange = { useFirebase = it },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = SwitchDefaults.colors(checkedThumbColor = Green, checkedTrackColor = GreenDark, uncheckedThumbColor = TextMuted, uncheckedTrackColor = SurfaceAlt)
                    )
                    Text("Personal", color = if (!useFirebase) Green else TextSecondary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(10.dp))
                if (useFirebase) {
                    Text("── Firebase Mode ON ──", color = Green, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    AdminField("API Key", firebaseApiKey, { firebaseApiKey = it }, false)
                    Spacer(Modifier.height(8.dp))
                    AdminField("Project ID", firebaseProjectId, { firebaseProjectId = it }, false)
                    Spacer(Modifier.height(8.dp))
                    AdminField("Auth Domain", firebaseAuthDomain, { firebaseAuthDomain = it }, false)
                } else {
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(GreenMuted).padding(12.dp)) {
                        Text("── Personal Mode ON ──\nBackend database use ho rahi hai (PostgreSQL)", color = Green, fontSize = 13.sp)
                    }
                }
            }

            // Save button
            Button(
                onClick = {
                    scope.launch {
                        saving = true
                        try {
                            RetrofitClient.api.updateAdminSettings("Bearer $accessToken", buildMap {
                                if (gmail.isNotBlank()) put("gmailUser", gmail)
                                if (appPassword.isNotBlank()) put("gmailAppPassword", appPassword)
                                if (smtpPort.isNotBlank()) put("smtpPort", smtpPort)
                            })
                            saved = true
                        } catch (_: Exception) {}
                        saving = false
                    }
                },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)
            ) {
                if (saving) CircularProgressIndicator(Modifier.size(22.dp), color = Background, strokeWidth = 2.dp)
                else Text("💾 Save All", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            if (saved) Text("✅ Saved!", color = Green, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun AdminSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface).padding(16.dp)) {
        Text(title, color = Green, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun AdminField(label: String, value: String, onValue: (String) -> Unit, readOnly: Boolean) {
    OutlinedTextField(
        value = value, onValueChange = onValue, label = { Text(label) },
        singleLine = true, readOnly = readOnly,
        modifier = Modifier.fillMaxWidth(), colors = outlinedFieldColors(), shape = RoundedCornerShape(10.dp)
    )
}
