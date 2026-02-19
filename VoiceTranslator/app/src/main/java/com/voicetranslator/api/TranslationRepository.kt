package com.voicetranslator.api

import android.content.Context
import com.voicetranslator.BuildConfig
import com.voicetranslator.data.DatabaseHelper
import com.voicetranslator.data.Translation

/**
 * Repository layer — bridges ViewModel with API and local database.
 * Follows Repository pattern for clean separation of concerns.
 */
class TranslationRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService
    private val dbHelper = DatabaseHelper(context)

    /**
     * Sealed class representing all possible translation results.
     */
    sealed class TranslationResult {
        data class Success(val translatedText: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }

    /**
     * Performs translation via RapidAPI Deep Translate.
     * Saves result to SQLite on success.
     *
     * @param text Text to translate.
     * @param sourceLang ISO language code for source (e.g. "en").
     * @param targetLang ISO language code for target (e.g. "es").
     * @return TranslationResult (Success or Error).
     */
    suspend fun translate(
        text: String,
        sourceLang: String,
        targetLang: String
    ): TranslationResult {
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

                if (!translatedText.isNullOrBlank()) {
                    // Save to SQLite database
                    saveToHistory(text, translatedText, sourceLang, targetLang)
                    TranslationResult.Success(translatedText)
                } else {
                    TranslationResult.Error("Translation returned empty result.")
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Invalid API key. Please check your RapidAPI credentials."
                    403 -> "API access denied. Subscribe to Deep Translate on RapidAPI."
                    429 -> "API rate limit exceeded. Please try again later."
                    500 -> "Server error. Please try again."
                    else -> "Translation failed: ${response.code()} ${response.message()}"
                }
                TranslationResult.Error(errorMsg)
            }

        } catch (e: java.net.UnknownHostException) {
            TranslationResult.Error("No internet connection. Please check your network.")
        } catch (e: java.net.SocketTimeoutException) {
            TranslationResult.Error("Connection timed out. Please try again.")
        } catch (e: Exception) {
            TranslationResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    /**
     * Saves a completed translation to the local SQLite database.
     */
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

    /**
     * Returns all stored translations from SQLite.
     */
    fun getHistory(): List<Translation> = dbHelper.getAllTranslations()

    /**
     * Deletes a single translation by ID.
     */
    fun deleteTranslation(id: Long) = dbHelper.deleteTranslation(id)

    /**
     * Clears all translation history.
     */
    fun clearHistory() = dbHelper.clearAllHistory()
}
