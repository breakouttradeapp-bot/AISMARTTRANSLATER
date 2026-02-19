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

// 🔥 REQUEST
data class TranslateRequest(
    val q: String,
    val source: String,
    val target: String
)

// 🔥 RESPONSE (IMPORTANT FIX)
data class TranslateResponse(
    val data: TranslateData?
)

data class TranslateData(
    val translations: Translations?
)

data class Translations(
    val translatedText: List<String>?   // ARRAY
)

// 🔥 API SERVICE
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

// 🔥 RETROFIT CLIENT
object RetrofitClient {

    private const val BASE_URL = "https://deep-translate1.p.rapidapi.com/"

    private val client: OkHttpClient by lazy {
        val log = HttpLoggingInterceptor()
        log.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .addInterceptor(log)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: TranslationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApiService::class.java)
    }
}

