package com.chatapp.network

import com.chatapp.network.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/signup")
    suspend fun signup(@Body req: SignupRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body req: RefreshRequest): Response<AuthResponse>

    @POST("auth/send-otp")
    suspend fun sendOtp(@Body req: SendOtpRequest): Response<GenericResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body req: VerifyOtpRequest): Response<GenericResponse>

    // Users
    @GET("users/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<UserResponse>

    @PATCH("users/me")
    suspend fun updateMe(@Header("Authorization") token: String, @Body req: UpdateProfileRequest): Response<UserResponse>

    @Multipart
    @PUT("users/me/avatar")
    suspend fun uploadAvatar(@Header("Authorization") token: String, @Part file: MultipartBody.Part): Response<UserResponse>

    @GET("users/search")
    suspend fun searchUsers(@Header("Authorization") token: String, @Query("q") query: String): Response<UsersResponse>

    @GET("users/{userId}")
    suspend fun getUser(@Header("Authorization") token: String, @Path("userId") userId: String): Response<UserResponse>

    // Servers
    @GET("servers")
    suspend fun getServers(@Header("Authorization") token: String): Response<ServersResponse>

    @POST("servers")
    suspend fun createServer(@Header("Authorization") token: String, @Body req: CreateServerRequest): Response<ServerResponse>

    @POST("servers/join")
    suspend fun joinServer(@Header("Authorization") token: String, @Body req: JoinServerRequest): Response<ServerResponse>

    @GET("servers/{serverId}")
    suspend fun getServer(@Header("Authorization") token: String, @Path("serverId") serverId: String): Response<ServerResponse>

    @POST("servers/{serverId}/invite")
    suspend fun regenerateInvite(@Header("Authorization") token: String, @Path("serverId") serverId: String): Response<InviteResponse>

    @POST("servers/{serverId}/members")
    suspend fun addMember(@Header("Authorization") token: String, @Path("serverId") serverId: String, @Body req: AddMemberRequest): Response<GenericResponse>

    @DELETE("servers/{serverId}/members/{memberId}")
    suspend fun removeMember(@Header("Authorization") token: String, @Path("serverId") serverId: String, @Path("memberId") memberId: String): Response<GenericResponse>

    @Multipart
    @POST("servers/{serverId}/icon")
    suspend fun uploadServerIcon(@Header("Authorization") token: String, @Path("serverId") serverId: String, @Part file: MultipartBody.Part): Response<ServerResponse>

    // Messages
    @GET("servers/{serverId}/messages")
    suspend fun getMessages(@Header("Authorization") token: String, @Path("serverId") serverId: String, @Query("before") before: String? = null, @Query("limit") limit: Int = 50): Response<MessagesResponse>

    @POST("servers/{serverId}/messages")
    suspend fun sendMessage(@Header("Authorization") token: String, @Path("serverId") serverId: String, @Body req: SendMessageRequest): Response<MessageResponse>

    @Multipart
    @POST("servers/{serverId}/messages/upload")
    suspend fun uploadMedia(@Header("Authorization") token: String, @Path("serverId") serverId: String, @Part file: MultipartBody.Part): Response<UploadResponse>

    // DMs
    @GET("chats")
    suspend fun getChats(@Header("Authorization") token: String): Response<ChatsResponse>

    @GET("chats/{userId}/messages")
    suspend fun getDmMessages(@Header("Authorization") token: String, @Path("userId") userId: String, @Query("before") before: String? = null): Response<MessagesResponse>

    @POST("chats/{userId}/messages")
    suspend fun sendDm(@Header("Authorization") token: String, @Path("userId") userId: String, @Body req: SendDmRequest): Response<MessageResponse>

    // Admin
    @GET("admin/stats")
    suspend fun getAdminStats(@Header("Authorization") token: String): Response<AdminStatsResponse>

    @GET("admin/settings")
    suspend fun getAdminSettings(@Header("Authorization") token: String): Response<AdminSettingsResponse>

    @PUT("admin/settings")
    suspend fun updateAdminSettings(@Header("Authorization") token: String, @Body settings: Map<String, String>): Response<GenericResponse>

    @PUT("admin/otp-template")
    suspend fun updateOtpTemplate(@Header("Authorization") token: String, @Body body: Map<String, String>): Response<GenericResponse>
}
