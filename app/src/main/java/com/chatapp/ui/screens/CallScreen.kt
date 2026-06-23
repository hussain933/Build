package com.chatapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.models.User
import com.chatapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CallScreen(otherUser: User, onEndCall: () -> Unit) {
    var callState by remember { mutableStateOf("ringing") } // ringing, active, ended
    var seconds by remember { mutableIntStateOf(0) }
    var muted by remember { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(false) }
    val pulse = remember { Animatable(1f) }

    LaunchedEffect(callState) {
        if (callState == "ringing") {
            while (callState == "ringing") {
                pulse.animateTo(1.15f, animationSpec = tween(700))
                pulse.animateTo(1f, animationSpec = tween(700))
            }
        }
    }
    LaunchedEffect(callState) {
        if (callState == "active") { while (callState == "active") { delay(1000); seconds++ } }
    }

    fun formatTime(s: Int) = "%02d:%02d".format(s / 60, s % 60)

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(GreenDark.copy(alpha = 0.8f), Background))
        )
    ) {
        Column(Modifier.fillMaxSize().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {

            // Name & status
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(24.dp))
                Text(otherUser.nickname, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(10.dp))

                // Avatar with pulse ring
                Box(Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                    if (callState == "ringing") {
                        Box(Modifier.size(160.dp).scale(pulse.value).clip(CircleShape).background(Green.copy(alpha = 0.1f)))
                        Box(Modifier.size(140.dp).scale(pulse.value).clip(CircleShape).background(Green.copy(alpha = 0.15f)))
                    }
                    Box(Modifier.size(120.dp).clip(CircleShape).background(GreenDark).align(Alignment.Center), contentAlignment = Alignment.Center) {
                        Text(otherUser.nickname.take(1).uppercase(), fontSize = 46.sp, fontWeight = FontWeight.Bold, color = Green)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    when (callState) {
                        "ringing" -> "🔔 Ringing..."
                        "active"  -> formatTime(seconds)
                        else      -> "Call Ended"
                    },
                    fontSize = 16.sp,
                    color = when (callState) { "ended" -> Red; else -> Green },
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Buttons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (callState == "ringing") {
                    Row(horizontalArrangement = Arrangement.spacedBy(60.dp)) {
                        CallBtn(icon = Icons.Default.CallEnd, label = "Decline", color = Red) { callState = "ended"; onEndCall() }
                        CallBtn(icon = Icons.Default.Call, label = "Accept", color = Green) { callState = "active" }
                    }
                }
                if (callState == "active") {
                    Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
                        SmallCallBtn(if (muted) Icons.Default.MicOff else Icons.Default.Mic, if (muted) "Unmute" else "Mute", muted) { muted = !muted }
                        CallBtn(Icons.Default.CallEnd, "End", Red) { callState = "ended"; onEndCall() }
                        SmallCallBtn(if (speaker) Icons.Default.VolumeUp else Icons.Default.VolumeDown, "Speaker", speaker) { speaker = !speaker }
                    }
                }
                if (callState == "ended") {
                    TextButton(onClick = onEndCall) { Text("Wapis Jao", color = TextSecondary, fontSize = 15.sp) }
                }
            }
        }
    }
}

@Composable
private fun CallBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick, modifier = Modifier.size(70.dp).clip(CircleShape).background(color)) {
            Icon(icon, null, tint = TextPrimary, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun SmallCallBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick, modifier = Modifier.size(56.dp).clip(CircleShape).background(if (active) GreenDark else SurfaceAlt)) {
            Icon(icon, null, tint = if (active) Green else TextSecondary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}
