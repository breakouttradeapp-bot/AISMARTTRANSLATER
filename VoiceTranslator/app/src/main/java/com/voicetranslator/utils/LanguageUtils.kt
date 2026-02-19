package com.voicetranslator.utils

/**
 * Utility object providing language code mappings for Deep Translate API.
 */
object LanguageUtils {

    /**
     * Full list of supported languages with display names and codes.
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
        "Hmong" to "hmn",
        "Hungarian" to "hu",
        "Icelandic" to "is",
        "Igbo" to "ig",
        "Indonesian" to "id",
        "Irish" to "ga",
        "Italian" to "it",
        "Japanese" to "ja",
        "Javanese" to "jv",
        "Kannada" to "kn",
        "Kazakh" to "kk",
        "Khmer" to "km",
        "Kinyarwanda" to "rw",
        "Korean" to "ko",
        "Kurdish" to "ku",
        "Kyrgyz" to "ky",
        "Lao" to "lo",
        "Latin" to "la",
        "Latvian" to "lv",
        "Lithuanian" to "lt",
        "Luxembourgish" to "lb",
        "Macedonian" to "mk",
        "Malagasy" to "mg",
        "Malay" to "ms",
        "Malayalam" to "ml",
        "Maltese" to "mt",
        "Maori" to "mi",
        "Marathi" to "mr",
        "Mongolian" to "mn",
        "Myanmar (Burmese)" to "my",
        "Nepali" to "ne",
        "Norwegian" to "no",
        "Nyanja (Chichewa)" to "ny",
        "Odia (Oriya)" to "or",
        "Pashto" to "ps",
        "Persian" to "fa",
        "Polish" to "pl",
        "Portuguese" to "pt",
        "Punjabi" to "pa",
        "Romanian" to "ro",
        "Russian" to "ru",
        "Samoan" to "sm",
        "Scots Gaelic" to "gd",
        "Serbian" to "sr",
        "Sesotho" to "st",
        "Shona" to "sn",
        "Sindhi" to "sd",
        "Sinhala" to "si",
        "Slovak" to "sk",
        "Slovenian" to "sl",
        "Somali" to "so",
        "Spanish" to "es",
        "Sundanese" to "su",
        "Swahili" to "sw",
        "Swedish" to "sv",
        "Tagalog (Filipino)" to "tl",
        "Tajik" to "tg",
        "Tamil" to "ta",
        "Tatar" to "tt",
        "Telugu" to "te",
        "Thai" to "th",
        "Turkish" to "tr",
        "Turkmen" to "tk",
        "Ukrainian" to "uk",
        "Urdu" to "ur",
        "Uyghur" to "ug",
        "Uzbek" to "uz",
        "Vietnamese" to "vi",
        "Welsh" to "cy",
        "Xhosa" to "xh",
        "Yiddish" to "yi",
        "Yoruba" to "yo",
        "Zulu" to "zu"
    )

    /** Returns display names sorted alphabetically */
    val displayNames: List<String> get() = languages.map { it.first }

    /** Returns language code from display name */
    fun getCode(displayName: String): String =
        languages.firstOrNull { it.first == displayName }?.second ?: "en"

    /** Returns display name from language code */
    fun getName(code: String): String =
        languages.firstOrNull { it.second == code }?.first ?: "English"

    /** Returns index in list from language code */
    fun getIndexByCode(code: String): Int =
        languages.indexOfFirst { it.second == code }.coerceAtLeast(0)
}
