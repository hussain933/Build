package com.chatapp.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*

data class WsMessage(val event: String, val data: JsonObject?)

object WebSocketClient {
    private val TAG = "WebSocketClient"
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private var token: String? = null

    private val _events = MutableSharedFlow<WsMessage>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    fun connect(accessToken: String) {
        token = accessToken
        val request = Request.Builder()
            .url("${RetrofitClient.WS_URL}?token=$accessToken")
            .build()
        webSocket = RetrofitClient.okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(TAG, "WS connected")
            }
            override fun onMessage(ws: WebSocket, text: String) {
                try {
                    val obj = JsonParser.parseString(text).asJsonObject
                    val event = obj.get("event")?.asString ?: return
                    _events.tryEmit(WsMessage(event, obj))
                } catch (e: Exception) {
                    Log.e(TAG, "WS parse error", e)
                }
            }
            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WS failure: ${t.message}")
            }
            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WS closed: $reason")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "User logout")
        webSocket = null
        token = null
    }

    fun isConnected() = webSocket != null
}
