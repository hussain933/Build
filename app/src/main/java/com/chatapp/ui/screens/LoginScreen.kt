package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.LoginRequest
import com.chatapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String, String, com.chatapp.network.models.User) -> Unit, onGoSignup: () -> Unit) {
    val scope = rememberCoroutineScope()
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(Background)) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

            Text("💬", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text("ChatApp", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Green)
            Spacer(Modifier.height(4.dp))
            Text("Login karo apne account mein", fontSize = 14.sp, color = TextSecondary)
            Spacer(Modifier.height(36.dp))

            // Card
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Surface).padding(24.dp)) {

                Text("Login", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = login, onValueChange = { login = it; errorMsg = "" },
                    label = { Text("Username ya Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(14.dp))

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

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (login.isBlank() || password.isBlank()) { errorMsg = "Sab fields bharo"; return@Button }
                        scope.launch {
                            loading = true; errorMsg = ""
                            try {
                                val res = RetrofitClient.api.login(LoginRequest(login.trim(), password))
                                if (res.isSuccessful && res.body()?.success == true) {
                                    val data = res.body()!!.data!!
                                    onLoginSuccess(data.accessToken, data.refreshToken, data.user)
                                } else {
                                    errorMsg = res.body()?.error?.message ?: "Login fail hua"
                                }
                            } catch (e: Exception) {
                                errorMsg = "Network error: ${e.message}"
                            }
                            loading = false
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(22.dp), color = Background, strokeWidth = 2.dp)
                    else Text("Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(20.dp))
            Row {
                Text("Account nahi hai? ", color = TextSecondary, fontSize = 14.sp)
                Text("Sign Up", color = Green, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onGoSignup() })
            }
        }
    }
}

@Composable
fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Green,
    unfocusedBorderColor = Border,
    focusedLabelColor = Green,
    unfocusedLabelColor = TextSecondary,
    cursorColor = Green,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    unfocusedContainerColor = SurfaceAlt,
    focusedContainerColor = SurfaceAlt,
)
