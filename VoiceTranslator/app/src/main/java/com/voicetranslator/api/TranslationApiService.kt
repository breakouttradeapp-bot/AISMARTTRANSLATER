package com.voicetranslator.api

import com.voicetranslator.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// ─── Request / Response models ──────────────────────────────────────────────

/**
 * Request payload sent to Deep Translate API.
 */
data class TranslateRequest(
    val q: String,        // Text to translate
    val source: String,   // Source language code (e.g. "en")
    val target: String    // Target language code (e.g. "es")
)

/**
 * Top-level response from Deep Translate API.
 */
data class TranslateResponse(
    val data: TranslateData?
)

data class TranslateData(
    val translations: TranslationResult?
)

data class TranslationResult(
    val translatedText: String?
)

// ─── Retrofit Interface ──────────────────────────────────────────────────────

/**
 * Retrofit API interface for Deep Translate v2.
 * API key injected via headers using BuildConfig for security.
 */
interface TranslationApiService {

    @Headers(
        "Content-Type: application/json",
        "x-rapidapi-host: deep-translate1.p.rapidapi.com"
    )
    @POST("language/translate/v2")
    suspend fun translate(
        @retrofit2.http.Header("x-rapidapi-key") apiKey: String = BuildConfig.RAPID_API_KEY,
        @Body request: TranslateRequest
    ): Response<TranslateResponse>
}

// ─── Retrofit Singleton ──────────────────────────────────────────────────────

/**
 * Singleton factory for building the Retrofit client.
 */
object RetrofitClient {

    private const val BASE_URL = "https://deep-translate1.p.rapidapi.com/"
    private const val TIMEOUT_SECONDS = 30L

    // OkHttp client with logging and timeouts
    private val okHttpClient: OkHttpClient by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit instance
    val apiService: TranslationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApiService::class.java)
    }
}
