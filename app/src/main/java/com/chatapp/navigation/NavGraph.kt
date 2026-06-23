package com.chatapp.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chatapp.network.models.Server
import com.chatapp.network.models.User
import com.chatapp.ui.screens.*

sealed class Screen(val route: String) {
    object Login        : Screen("login")
    object Signup       : Screen("signup")
    object Otp          : Screen("otp")
    object Home         : Screen("home")
    object Contacts     : Screen("contacts")
    object Profile      : Screen("profile")
    object EditProfile  : Screen("edit_profile")
    object ServerChat   : Screen("server_chat")
    object DmChat       : Screen("dm_chat")
    object AddMembers   : Screen("add_members")
    object CreateServer : Screen("create_server")
    object JoinServer   : Screen("join_server")
    object Call         : Screen("call")
    object VideoCall    : Screen("video_call")
    object Report       : Screen("report")
    object Admin        : Screen("admin")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    accessToken: String,
    myUserId: String,
    currentUser: User?,
    onLoginSuccess: (String, String, User) -> Unit,
    onLogout: () -> Unit,
    onUpdateUser: (User) -> Unit
) {
    var selectedServer by remember { mutableStateOf<Server?>(null) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    NavHost(navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { at, rt, user -> onLoginSuccess(at, rt, user); navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onGoSignup = { navController.navigate(Screen.Signup.route) }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = { at, rt, user -> onLoginSuccess(at, rt, user); navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onGoLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Otp.route) {
            OtpScreen(
                onVerified = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                accessToken = accessToken,
                onServerClick = { server -> selectedServer = server; navController.navigate(Screen.ServerChat.route) },
                onGoContacts = { navController.navigate(Screen.Contacts.route) },
                onGoProfile = { navController.navigate(Screen.Profile.route) },
                onCreateServer = { navController.navigate(Screen.CreateServer.route) },
                onJoinServer = { navController.navigate(Screen.JoinServer.route) }
            )
        }

        composable(Screen.Contacts.route) {
            ContactsScreen(
                accessToken = accessToken,
                onChatClick = { user -> selectedUser = user; navController.navigate(Screen.DmChat.route) },
                onGoHome = { navController.navigate(Screen.Home.route) },
                onGoProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                accessToken = accessToken,
                cachedUser = currentUser,
                onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onLogout = { onLogout(); navController.navigate(Screen.Login.route) { popUpTo(0) } },
                onGoHome = { navController.navigate(Screen.Home.route) },
                onGoContacts = { navController.navigate(Screen.Contacts.route) },
                onAdminPanel = if (currentUser?.role == "admin") {{ navController.navigate(Screen.Admin.route) }} else null
            )
        }

        composable(Screen.EditProfile.route) {
            if (currentUser != null) {
                EditProfileScreen(
                    accessToken = accessToken,
                    currentUser = currentUser,
                    onBack = { navController.popBackStack() },
                    onSaved = { user -> onUpdateUser(user); navController.popBackStack() }
                )
            }
        }

        composable(Screen.ServerChat.route) {
            if (selectedServer != null) {
                ServerChatScreen(
                    accessToken = accessToken,
                    myUserId = myUserId,
                    server = selectedServer!!,
                    onBack = { navController.popBackStack() },
                    onAddMembers = { navController.navigate(Screen.AddMembers.route) }
                )
            }
        }

        composable(Screen.DmChat.route) {
            if (selectedUser != null) {
                    DmChatScreen(userId = userId, onBack = { navController.popBackStack() })
                    onCall = { navController.navigate(Screen.Call.route) },
                    onVideoCall = { navController.navigate(Screen.VideoCall.route) },
                    onReport = { navController.navigate(Screen.Report.route) },
                    onBlock = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.AddMembers.route) {
            if (selectedServer != null) {
                AddMembersScreen(
                    accessToken = accessToken,
                    serverId = selectedServer!!.id,
                    inviteCode = selectedServer!!.inviteCode ?: "",
                    memberUserIds = selectedServer!!.members?.map { it.userId } ?: emptyList(),
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.CreateServer.route) {
            CreateServerScreen(
                accessToken = accessToken,
                onBack = { navController.popBackStack() },
                onCreated = { server -> selectedServer = server; navController.navigate(Screen.ServerChat.route) { popUpTo(Screen.Home.route) } }
            )
        }

        composable(Screen.JoinServer.route) {
            JoinServerScreen(
                accessToken = accessToken,
                onBack = { navController.popBackStack() },
                onJoined = { server -> selectedServer = server; navController.navigate(Screen.ServerChat.route) { popUpTo(Screen.Home.route) } }
            )
        }

        composable(Screen.Call.route) {
            if (selectedUser != null) {
                CallScreen(otherUser = selectedUser!!, onEndCall = { navController.popBackStack() })
            }
        }

        composable(Screen.VideoCall.route) {
            if (selectedUser != null) {
                VideoCallScreen(otherUser = selectedUser!!, onEndCall = { navController.popBackStack() })
            }
        }

        composable(Screen.Report.route) {
            if (selectedUser != null) {
                ReportScreen(
                    reportedUser = selectedUser!!,
                    onBack = { navController.popBackStack() },
                    onSubmitted = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Admin.route) {
            AdminScreen(accessToken = accessToken, onBack = { navController.popBackStack() })
        }
    }
}
