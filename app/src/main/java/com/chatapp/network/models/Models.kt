package com.chatapp.network.models

data class SignupRequest(val username: String, val password: String, val nickname: String, val email: String? = null)
data class LoginRequest(val login: String, val password: String)
data class RefreshRequest(val refreshToken: String)
data class SendOtpRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String)
data class UpdateProfileRequest(val nickname: String? = null, val avatarUrl: String? = null)
data class CreateServerRequest(val name: String, val iconUrl: String? = null)
data class JoinServerRequest(val inviteCode: String)
data class AddMemberRequest(val userId: String)
data class SendMessageRequest(val content: String, val type: String = "text")
data class SendDmRequest(val content: String, val type: String = "text")

data class AuthResponse(
    val success: Boolean,
    val data: AuthData? = null,
    val error: ApiError? = null
)
data class AuthData(val accessToken: String, val refreshToken: String, val user: User)

data class UserResponse(val success: Boolean, val data: User? = null, val error: ApiError? = null)
data class UsersResponse(val success: Boolean, val data: List<User>? = null, val error: ApiError? = null)

data class User(
    val id: String,
    val username: String,
    val nickname: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val emailVerified: Boolean = false,
    val role: String = "user"
)

data class ServerResponse(val success: Boolean, val data: Server? = null, val error: ApiError? = null)
data class ServersResponse(val success: Boolean, val data: List<Server>? = null, val error: ApiError? = null)
data class Server(
    val id: String,
    val name: String,
    val iconUrl: String? = null,
    val inviteCode: String? = null,
    val ownerId: String,
    val members: List<Member>? = null
)
data class Member(val id: String, val userId: String, val serverId: String, val role: String, val user: User? = null)

data class MessagesResponse(val success: Boolean, val data: List<Message>? = null, val error: ApiError? = null)
data class MessageResponse(val success: Boolean, val data: Message? = null, val error: ApiError? = null)
data class Message(
    val id: String,
    val content: String,
    val type: String = "text",
    val senderId: String,
    val serverId: String? = null,
    val receiverId: String? = null,
    val createdAt: String,
    val sender: User? = null
)

data class ChatsResponse(val success: Boolean, val data: List<ChatPreview>? = null, val error: ApiError? = null)
data class ChatPreview(val userId: String, val user: User, val lastMessage: Message? = null, val unreadCount: Int = 0)

data class InviteResponse(val success: Boolean, val data: InviteData? = null, val error: ApiError? = null)
data class InviteData(val inviteCode: String)

data class AdminStatsResponse(val success: Boolean, val data: AdminStats? = null)
data class AdminStats(val totalUsers: Int, val totalServers: Int, val totalMessages: Int, val onlineUsers: Int)

data class AdminSettingsResponse(val success: Boolean, val data: AdminSettings? = null)
data class AdminSettings(
    val gmailUser: String? = null,
    val gmailAppPassword: String? = null,
    val smtpPort: Int? = 587,
    val otpTemplate: String? = null
)

data class UploadResponse(val success: Boolean, val data: UploadData? = null, val error: ApiError? = null)
data class UploadData(val url: String)

data class ApiError(val code: String, val message: String)
data class GenericResponse(val success: Boolean, val error: ApiError? = null)
