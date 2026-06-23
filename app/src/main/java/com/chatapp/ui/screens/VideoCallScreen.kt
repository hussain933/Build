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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.models.User
import com.chatapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoCallScreen(otherUser: User, onEndCall: () -> Unit) {
    var seconds by remember { mutableIntStateOf(0) }
    var muted by remember { mutableStateOf(false) }
    var cameraOff by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { while (true) { delay(1000); seconds++ } }
    fun fmt(s: Int) = "%02d:%02d".format(s / 60, s % 60)

    Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black)) {

        // Remote video (full screen) — simulated with dark gradient
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(GreenDark.copy(alpha = 0.15f), androidx.compose.ui.graphics.Color.Black))),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(100.dp).clip(CircleShape).background(GreenDark), contentAlignment = Alignment.Center) {
                    Text(otherUser.nickname.take(1).uppercase(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Green)
                }
                Spacer(Modifier.height(12.dp))
                Text(otherUser.nickname, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Top bar: name + timer
        Column(Modifier.fillMaxWidth().padding(top = 44.dp, start = 20.dp, end = 20.dp)) {
            Text(otherUser.nickname, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(fmt(seconds), color = Green, fontSize = 14.sp)
        }

        // My small video (top-right)
        Box(
            Modifier.size(width = 100.dp, height = 140.dp)
                .align(Alignment.TopEnd)
                .padding(top = 36.dp, end = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (cameraOff) SurfaceAlt else GreenDark),
            contentAlignment = Alignment.Center
        ) {
            if (cameraOff) Icon(Icons.Default.VideocamOff, null, tint = TextMuted, modifier = Modifier.size(28.dp))
            else Text("You", color = Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        // Bottom controls
        Row(
            Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color.Transparent, androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.8f))))
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VideoCallBtn(if (cameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam, cameraOff) { cameraOff = !cameraOff }
            VideoCallBtn(if (muted) Icons.Default.MicOff else Icons.Default.Mic, muted) { muted = !muted }
            IconButton(onClick = onEndCall, modifier = Modifier.size(64.dp).clip(CircleShape).background(Red)) {
                Icon(Icons.Default.CallEnd, null, tint = TextPrimary, modifier = Modifier.size(28.dp))
            }
            VideoCallBtn(Icons.Default.Cameraswitch, false) {}
        }
    }
}

@Composable
private fun VideoCallBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(52.dp).clip(CircleShape).background(if (active) SurfaceAlt else SurfaceAlt.copy(alpha = 0.7f))) {
        Icon(icon, null, tint = if (active) Red else TextSecondary, modifier = Modifier.size(22.dp))
    }
}
