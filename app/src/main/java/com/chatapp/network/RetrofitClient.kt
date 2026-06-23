package com.chatapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // ─── Change this URL if you redeploy the backend ───────────────────────────
    const val BASE_URL = "https://ee7bc21f-b2d7-429c-90f7-4db5c5a51203-00-gdz2mgezl6uw.sisko.replit.dev/api/v1/"
    const val WS_URL   = "wss://ee7bc21f-b2d7-429c-90f7-4db5c5a51203-00-gdz2mgezl6uw.sisko.replit.dev/ws"
    // ───────────────────────────────────────────────────────────────────────────

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
