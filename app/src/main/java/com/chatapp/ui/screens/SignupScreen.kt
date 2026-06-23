package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.SignupRequest
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(
    onSignupSuccess: (String, String, com.chatapp.network.models.User) -> Unit,
    onGoLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var nickname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(Background)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("💬", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text("ChatApp", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Green)
            Spacer(Modifier.height(4.dp))
            Text("Naya account banao", fontSize = 14.sp, color = TextSecondary)
            Spacer(Modifier.height(28.dp))

            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Surface).padding(24.dp)) {
                Text("Profile Banao", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text("Apna naam aur username set karo", fontSize = 13.sp, color = TextSecondary)
                Spacer(Modifier.height(20.dp))

                // Avatar placeholder
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    Box(
                        Modifier.size(80.dp).clip(RoundedCornerShape(40.dp)).background(GreenDark)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📷", fontSize = 28.sp)
                    }
                }
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = nickname, onValueChange = { nickname = it; errorMsg = "" },
                    label = { Text("Nickname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = username, onValueChange = { username = it.lowercase().replace(" ", ""); errorMsg = "" },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Text("@", color = TextSecondary, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp)) }
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it; errorMsg = "" },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = TextSecondary)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMsg.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(errorMsg, color = Red, fontSize = 13.sp)
                }
                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = {
                        when {
                            nickname.isBlank() -> { errorMsg = "Nickname likho"; return@Button }
                            username.isBlank() -> { errorMsg = "Username likho"; return@Button }
                            password.length < 6 -> { errorMsg = "Password kam se kam 6 letters ka ho"; return@Button }
                        }
                        scope.launch {
                            loading = true; errorMsg = ""
                            try {
                                val res = RetrofitClient.api.signup(SignupRequest(username.trim(), password, nickname.trim()))
                                if (res.isSuccessful && res.body()?.success == true) {
                                    val data = res.body()!!.data!!
                                    onSignupSuccess(data.accessToken, data.refreshToken, data.user)
                                } else {
                                    errorMsg = res.body()?.error?.message ?: "Signup fail hua"
                                }
                            } catch (e: Exception) { errorMsg = "Network error: ${e.message}" }
                            loading = false
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(22.dp), color = Background, strokeWidth = 2.dp)
                    else Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(20.dp))
            Row {
                Text("Account hai? ", color = TextSecondary, fontSize = 14.sp)
                Text("Login", color = Green, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onGoLogin() })
            }
        }
    }
}
