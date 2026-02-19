package com.voicetranslator.api

import android.content.Context
import com.voicetranslator.BuildConfig
import com.voicetranslator.data.DatabaseHelper
import com.voicetranslator.data.Translation

class TranslationRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService
    private val dbHelper = DatabaseHelper(context)

    sealed class TranslationResult {
        data class Success(val translatedText: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }

    suspend fun translate(
        text: String,
        sourceLang: String,
        targetLang: String
    ): TranslationResult {

        // 🚨 If same language selected → don't call API
        if (sourceLang == targetLang) {
            return TranslationResult.Success(text)
        }

        return try {

            val request = TranslateRequest(
                q = text,
                source = sourceLang,
                target = targetLang
            )

            val response = apiService.translate(
                apiKey = BuildConfig.RAPID_API_KEY,
                request = request
            )

            if (response.isSuccessful) {

                val translatedText = response.body()
                    ?.data
                    ?.translations
                    ?.translatedText
                    ?.firstOrNull()

                if (!translatedText.isNullOrEmpty()) {
                    saveToHistory(text, translatedText, sourceLang, targetLang)
                    TranslationResult.Success(translatedText)
                } else {
                    TranslationResult.Error("Translation failed")
                }

            } else {
                TranslationResult.Error("API Error ${response.code()}")
            }

        } catch (e: Exception) {
            TranslationResult.Error("Error: ${e.message}")
        }
    }

    private fun saveToHistory(
        inputText: String,
        translatedText: String,
        sourceLang: String,
        targetLang: String
    ) {
        val translation = Translation(
            inputText = inputText,
            translatedText = translatedText,
            sourceLang = sourceLang,
            targetLang = targetLang,
            timestamp = System.currentTimeMillis()
        )
        dbHelper.insertTranslation(translation)
    }

    fun getHistory(): List<Translation> = dbHelper.getAllTranslations()
    fun deleteTranslation(id: Long) = dbHelper.deleteTranslation(id)
    fun clearHistory() = dbHelper.clearAllHistory()
}

