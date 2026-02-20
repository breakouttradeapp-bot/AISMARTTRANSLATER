package com.niv.aivoicetranslator.data

/**
 * Data model representing a single translation record.
 */
data class Translation(
    val id: Long = 0,
    val inputText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val timestamp: Long = System.currentTimeMillis()
)
