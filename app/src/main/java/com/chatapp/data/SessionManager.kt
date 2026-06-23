package com.chatapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

object SessionKeys {
    val ACCESS_TOKEN  = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val USER_ID       = stringPreferencesKey("user_id")
    val USERNAME      = stringPreferencesKey("username")
    val NICKNAME      = stringPreferencesKey("nickname")
    val EMAIL         = stringPreferencesKey("email")
    val AVATAR_URL    = stringPreferencesKey("avatar_url")
    val ROLE          = stringPreferencesKey("role")
}

class SessionManager(private val context: Context) {

    val accessToken: Flow<String?> = context.dataStore.data.map { it[SessionKeys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[SessionKeys.REFRESH_TOKEN] }
    val userId: Flow<String?> = context.dataStore.data.map { it[SessionKeys.USER_ID] }
    val role: Flow<String?> = context.dataStore.data.map { it[SessionKeys.ROLE] }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[SessionKeys.ACCESS_TOKEN] != null }

    suspend fun saveSession(
        accessToken: String, refreshToken: String,
        userId: String, username: String, nickname: String,
        email: String?, avatarUrl: String?, role: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[SessionKeys.ACCESS_TOKEN]  = accessToken
            prefs[SessionKeys.REFRESH_TOKEN] = refreshToken
            prefs[SessionKeys.USER_ID]       = userId
            prefs[SessionKeys.USERNAME]      = username
            prefs[SessionKeys.NICKNAME]      = nickname
            email?.let { prefs[SessionKeys.EMAIL] = it }
            avatarUrl?.let { prefs[SessionKeys.AVATAR_URL] = it }
            prefs[SessionKeys.ROLE]          = role
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[SessionKeys.ACCESS_TOKEN]  = accessToken
            prefs[SessionKeys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    fun bearerToken(token: String) = "Bearer $token"
}
