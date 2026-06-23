package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.models.User
import com.chatapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(reportedUser: User, onBack: () -> Unit, onSubmitted: () -> Unit) {
    val reasons = listOf("Spam hai", "Fake account hai", "Galat msg bheja", "Kuch aur")
    var selectedReason by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Report ${reportedUser.nickname}", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        if (submitted) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Report Submit Ho Gaya", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Humari team review karegi", color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(28.dp))
                    Button(onClick = { onSubmitted() }, shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)) {
                        Text("Wapis Jao", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
                Text("Kyu report kar rahe ho?", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.height(20.dp))

                reasons.forEach { reason ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(if (selectedReason == reason) GreenDark else Surface)
                            .clickable { selectedReason = reason }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (selectedReason == reason) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                            null, tint = if (selectedReason == reason) Green else TextSecondary, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(reason, color = TextPrimary, fontSize = 15.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(8.dp))
                Text("Details likho...", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = details, onValueChange = { details = it },
                    placeholder = { Text("Zyada detail dena helpful hoga", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    colors = outlinedFieldColors(), shape = RoundedCornerShape(12.dp), maxLines = 5
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { if (selectedReason.isNotEmpty()) submitted = true },
                    enabled = selectedReason.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red, contentColor = TextPrimary, disabledContainerColor = SurfaceAlt, disabledContentColor = TextMuted)
                ) { Text("Submit Report", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
            }
        }
    }
}
