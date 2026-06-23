package com.chatapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.chatapp.data.SessionManager
import com.chatapp.data.SessionKeys
import com.chatapp.network.WebSocketClient
import com.chatapp.network.models.User
import com.chatapp.navigation.NavGraph
import com.chatapp.navigation.Screen
import com.chatapp.ui.theme.Background
import com.chatapp.ui.theme.ChatAppTheme
import com.chatapp.ui.theme.Green
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        val sessionManager = SessionManager(applicationContext)

        setContent {
            ChatAppTheme {
                val scope = rememberCoroutineScope()
                var isLoading by remember { mutableStateOf(true) }
                var startDest by remember { mutableStateOf(Screen.Login.route) }
                var accessToken by remember { mutableStateOf("") }
                var myUserId by remember { mutableStateOf("") }
                var currentUser by remember { mutableStateOf<User?>(null) }
                val navController = rememberNavController()

                // Check saved session on startup
                LaunchedEffect(Unit) {
                    val prefs = sessionManager.accessToken.first()
                    if (prefs != null) {
                        accessToken = prefs
//                         myUserId = applicationContext.dataStore.data.first()[SessionKeys.USER_ID] ?: ""
//                         val username = applicationContext.dataStore.data.first()[SessionKeys.USERNAME] ?: ""
//                         val nickname = applicationContext.dataStore.data.first()[SessionKeys.NICKNAME] ?: ""
//                         val email = applicationContext.dataStore.data.first()[SessionKeys.EMAIL]
//                         val avatarUrl = applicationContext.dataStore.data.first()[SessionKeys.AVATAR_URL]
//                         val role = applicationContext.dataStore.data.first()[SessionKeys.ROLE] ?: "user"
                        currentUser = User(id = myUserId, username = username, nickname = nickname, email = email, avatarUrl = avatarUrl, role = role)
                        startDest = Screen.Home.route
                        WebSocketClient.connect(prefs)
                    }
                    isLoading = false
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Green)
                    }
                } else {
                    NavGraph(
                        navController = navController,
                        startDestination = startDest,
                        accessToken = accessToken,
                        myUserId = myUserId,
                        currentUser = currentUser,
                        onLoginSuccess = { at, rt, user ->
                            accessToken = at; myUserId = user.id; currentUser = user
                            scope.launch {
                                sessionManager.saveSession(at, rt, user.id, user.username, user.nickname, user.email, user.avatarUrl, user.role)
                            }
                            WebSocketClient.connect(at)
                        },
                        onLogout = {
                            accessToken = ""; myUserId = ""; currentUser = null
                            WebSocketClient.disconnect()
                            scope.launch { sessionManager.clearSession() }
                        },
                        onUpdateUser = { user ->
                            currentUser = user
                            scope.launch {
                                sessionManager.saveSession(accessToken, "", user.id, user.username, user.nickname, user.email, user.avatarUrl, user.role)
                            }
                        }
                    )
                }
            }
        }
    }
}
