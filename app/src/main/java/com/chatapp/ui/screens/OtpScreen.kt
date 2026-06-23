package com.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.network.RetrofitClient
import com.chatapp.network.models.SendOtpRequest
import com.chatapp.network.models.VerifyOtpRequest
import com.chatapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(onVerified: () -> Unit, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var otpDigits by remember { mutableStateOf(List(6) { "" }) }
    var otpSent by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }
    var resendTimer by remember { mutableStateOf(0) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    LaunchedEffect(resendTimer) {
        if (resendTimer > 0) { delay(1000); resendTimer-- }
    }

    Box(Modifier.fillMaxSize().background(Background)) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
            Spacer(Modifier.height(20.dp))

            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Surface).padding(24.dp)) {
                Text("Email Verify Karo", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text("OTP aapke email pe bheja jayega", fontSize = 13.sp, color = TextSecondary)
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it; errorMsg = "" },
                    label = { Text("Email Address") },
                    singleLine = true,
                    enabled = !otpSent,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(Modifier.height(14.dp))

                if (!otpSent) {
                    Button(
                        onClick = {
                            if (email.isBlank()) { errorMsg = "Email likho"; return@Button }
                            scope.launch {
                                loading = true; errorMsg = ""
                                try {
                                    val res = RetrofitClient.api.sendOtp(SendOtpRequest(email.trim()))
                                    if (res.isSuccessful) {
                                        otpSent = true; resendTimer = 60
                                        successMsg = "OTP bhej diya! Email check karo"
                                    } else errorMsg = "OTP send nahi hua"
                                } catch (e: Exception) { errorMsg = "Network error" }
                                loading = false
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)
                    ) {
                        if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Background, strokeWidth = 2.dp)
                        else Text("OTP Bhejo", fontWeight = FontWeight.Bold)
                    }
                }

                if (otpSent) {
                    if (successMsg.isNotEmpty()) {
                        Text(successMsg, color = Green, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("${email.take(3)}****@${email.substringAfter("@")} pe code bheja hai", color = TextSecondary, fontSize = 12.sp)
                        Spacer(Modifier.height(20.dp))
                    }

                    // 6 OTP boxes
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        otpDigits.forEachIndexed { i, digit ->
                            OutlinedTextField(
                                value = digit,
                                onValueChange = { v ->
                                    val c = v.filter { it.isDigit() }.take(1)
                                    otpDigits = otpDigits.toMutableList().also { it[i] = c }
                                    if (c.isNotEmpty() && i < 5) focusRequesters[i + 1].requestFocus()
                                },
                                modifier = Modifier.weight(1f).focusRequester(focusRequesters[i]),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Green, unfocusedBorderColor = Border,
                                    focusedContainerColor = SurfaceAlt, unfocusedContainerColor = SurfaceAlt,
                                    cursorColor = Green
                                ),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    // Resend timer
                    if (resendTimer > 0) {
                        Text("Code nahi aaya? ${resendTimer}s baad dobara bhejo", color = TextSecondary, fontSize = 13.sp)
                    } else {
                        TextButton(onClick = {
                            scope.launch {
                                try { RetrofitClient.api.sendOtp(SendOtpRequest(email.trim())); resendTimer = 60 }
                                catch (_: Exception) {}
                            }
                        }) { Text("Code nahi aaya? Dobara bhejo", color = Green, fontSize = 13.sp) }
                    }
                    Spacer(Modifier.height(16.dp))

                    if (errorMsg.isNotEmpty()) { Text(errorMsg, color = Red, fontSize = 13.sp); Spacer(Modifier.height(8.dp)) }

                    Button(
                        onClick = {
                            val otp = otpDigits.joinToString("")
                            if (otp.length < 6) { errorMsg = "6 digit OTP likho"; return@Button }
                            scope.launch {
                                loading = true; errorMsg = ""
                                try {
                                    val res = RetrofitClient.api.verifyOtp(VerifyOtpRequest(email.trim(), otp))
                                    if (res.isSuccessful && res.body()?.success == true) onVerified()
                                    else errorMsg = res.body()?.error?.message ?: "Galat OTP"
                                } catch (e: Exception) { errorMsg = "Network error" }
                                loading = false
                            }
                        },
                        enabled = !loading && otpDigits.joinToString("").length == 6,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Background)
                    ) {
                        if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Background, strokeWidth = 2.dp)
                        else Text("Verify Karo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
