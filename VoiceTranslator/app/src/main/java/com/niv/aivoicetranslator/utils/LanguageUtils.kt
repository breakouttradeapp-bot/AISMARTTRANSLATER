package com.niv.aivoicetranslator.utils

/**
 * Utility object providing language code mappings for Deep Translate API.
 */
object LanguageUtils {

    /**
     * Full list of supported languages with display names and ISO codes.
     */
    val languages: List<Pair<String, String>> = listOf(
        "Afrikaans" to "af",
        "Albanian" to "sq",
        "Amharic" to "am",
        "Arabic" to "ar",
        "Armenian" to "hy",
        "Azerbaijani" to "az",
        "Basque" to "eu",
        "Belarusian" to "be",
        "Bengali" to "bn",
        "Bosnian" to "bs",
        "Bulgarian" to "bg",
        "Catalan" to "ca",
        "Cebuano" to "ceb",
        "Chinese (Simplified)" to "zh",
        "Chinese (Traditional)" to "zh-TW",
        "Corsican" to "co",
        "Croatian" to "hr",
        "Czech" to "cs",
        "Danish" to "da",
        "Dutch" to "nl",
        "English" to "en",
        "Esperanto" to "eo",
        "Estonian" to "et",
        "Finnish" to "fi",
        "French" to "fr",
        "Frisian" to "fy",
        "Galician" to "gl",
        "Georgian" to "ka",
        "German" to "de",
        "Greek" to "el",
        "Gujarati" to "gu",
        "Haitian Creole" to "ht",
        "Hausa" to "ha",
        "Hawaiian" to "haw",
        "Hebrew" to "he",
        "Hindi" to "hi",
        "Hungarian" to "hu",
        "Icelandic" to "is",
        "Indonesian" to "id",
        "Irish" to "ga",
        "Italian" to "it",
        "Japanese" to "ja",
        "Kannada" to "kn",
        "Kazakh" to "kk",
        "Khmer" to "km",
        "Korean" to "ko",
        "Lao" to "lo",
        "Latin" to "la",
        "Latvian" to "lv",
        "Lithuanian" to "lt",
        "Malay" to "ms",
        "Malayalam" to "ml",
        "Marathi" to "mr",
        "Nepali" to "ne",
        "Norwegian" to "no",
        "Persian" to "fa",
        "Polish" to "pl",
        "Portuguese" to "pt",
        "Punjabi" to "pa",
        "Romanian" to "ro",
        "Russian" to "ru",
        "Spanish" to "es",
        "Swahili" to "sw",
        "Swedish" to "sv",
        "Tamil" to "ta",
        "Telugu" to "te",
        "Thai" to "th",
        "Turkish" to "tr",
        "Ukrainian" to "uk",
        "Urdu" to "ur",
        "Vietnamese" to "vi",
        "Welsh" to "cy",
        "Yoruba" to "yo",
        "Zulu" to "zu"
    )

    /** Display names for Spinner */
    val displayNames: List<String>
        get() = languages.map { it.first }

    /** Get language code from display name */
    fun getCode(displayName: String): String =
        languages.firstOrNull { it.first == displayName }?.second ?: "en"

    /** Get display name from language code */
    fun getName(code: String): String =
        languages.firstOrNull { it.second == code }?.first ?: "English"

    /** Get index for spinner selection */
    fun getIndexByCode(code: String): Int =
        languages.indexOfFirst { it.second == code }
            .takeIf { it >= 0 } ?: 0
}

