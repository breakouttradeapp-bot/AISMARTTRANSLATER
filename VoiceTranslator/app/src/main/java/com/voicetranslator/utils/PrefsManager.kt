package com.voicetranslator.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages all SharedPreferences settings for the app.
 */
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("voice_translator_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SOURCE_LANG = "source_lang"
        private const val KEY_TARGET_LANG = "target_lang"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_TTS_ENABLED = "tts_enabled"
        private const val KEY_TTS_SPEED = "tts_speed"
        private const val KEY_TRANSLATION_COUNT = "translation_count"
    }

    // 🔥 Source language (Default = Hindi)
    var sourceLang: String
        get() = prefs.getString(KEY_SOURCE_LANG, "hi") ?: "hi"
        set(value) = prefs.edit().putString(KEY_SOURCE_LANG, value).apply()

    // 🔥 Target language (Default = English)
    var targetLang: String
        get() = prefs.getString(KEY_TARGET_LANG, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_TARGET_LANG, value).apply()

    // 0 = system default, 1 = light, 2 = dark
    var themeMode: Int
        get() = prefs.getInt(KEY_THEME_MODE, 0)
        set(value) = prefs.edit().putInt(KEY_THEME_MODE, value).apply()

    // TTS auto-play toggle
    var ttsEnabled: Boolean
        get() = prefs.getBoolean(KEY_TTS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_TTS_ENABLED, value).apply()

    // TTS speech rate (0.5 to 2.0)
    var ttsSpeed: Float
        get() = prefs.getFloat(KEY_TTS_SPEED, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_TTS_SPEED, value).apply()

    // Track translation count for interstitial ad trigger
    var translationCount: Int
        get() = prefs.getInt(KEY_TRANSLATION_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_TRANSLATION_COUNT, value).apply()

    fun incrementTranslationCount() {
        translationCount++
    }

    fun resetTranslationCount() {
        translationCount = 0
    }
}

