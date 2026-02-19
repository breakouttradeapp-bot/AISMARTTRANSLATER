package com.voicetranslator.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper manages all SQLite operations for translation history.
 * Stores the last 50 translations with auto-cleanup of older entries.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "voice_translator.db"
        private const val DATABASE_VERSION = 1
        private const val MAX_HISTORY_COUNT = 50

        // Table and column names
        const val TABLE_TRANSLATIONS = "translations"
        const val COL_ID = "id"
        const val COL_INPUT_TEXT = "input_text"
        const val COL_TRANSLATED_TEXT = "translated_text"
        const val COL_SOURCE_LANG = "source_lang"
        const val COL_TARGET_LANG = "target_lang"
        const val COL_TIMESTAMP = "timestamp"
    }

    // SQL to create translations table
    private val CREATE_TABLE = """
        CREATE TABLE $TABLE_TRANSLATIONS (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_INPUT_TEXT TEXT NOT NULL,
            $COL_TRANSLATED_TEXT TEXT NOT NULL,
            $COL_SOURCE_LANG TEXT NOT NULL,
            $COL_TARGET_LANG TEXT NOT NULL,
            $COL_TIMESTAMP INTEGER NOT NULL
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop old table and recreate on schema upgrade
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSLATIONS")
        onCreate(db)
    }

    /**
     * Inserts a new translation record.
     * Automatically trims history to last 50 entries after insert.
     *
     * @param translation The Translation object to save.
     * @return Row ID of the inserted record, -1 on failure.
     */
    fun insertTranslation(translation: Translation): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_INPUT_TEXT, translation.inputText)
            put(COL_TRANSLATED_TEXT, translation.translatedText)
            put(COL_SOURCE_LANG, translation.sourceLang)
            put(COL_TARGET_LANG, translation.targetLang)
            put(COL_TIMESTAMP, translation.timestamp)
        }

        val rowId = db.insert(TABLE_TRANSLATIONS, null, values)

        // Keep only the last 50 translations
        trimHistory(db)

        return rowId
    }

    /**
     * Retrieves all translations ordered by newest first.
     *
     * @return List of Translation objects.
     */
    fun getAllTranslations(): List<Translation> {
        val translations = mutableListOf<Translation>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_TRANSLATIONS,
            null,
            null,
            null,
            null,
            null,
            "$COL_TIMESTAMP DESC" // newest first
        )

        cursor.use {
            while (it.moveToNext()) {
                translations.add(
                    Translation(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                        inputText = it.getString(it.getColumnIndexOrThrow(COL_INPUT_TEXT)),
                        translatedText = it.getString(it.getColumnIndexOrThrow(COL_TRANSLATED_TEXT)),
                        sourceLang = it.getString(it.getColumnIndexOrThrow(COL_SOURCE_LANG)),
                        targetLang = it.getString(it.getColumnIndexOrThrow(COL_TARGET_LANG)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_TIMESTAMP))
                    )
                )
            }
        }

        return translations
    }

    /**
     * Deletes a single translation by its ID.
     *
     * @param id The ID of the record to delete.
     */
    fun deleteTranslation(id: Long) {
        writableDatabase.delete(TABLE_TRANSLATIONS, "$COL_ID = ?", arrayOf(id.toString()))
    }

    /**
     * Deletes all translation history.
     */
    fun clearAllHistory() {
        writableDatabase.delete(TABLE_TRANSLATIONS, null, null)
    }

    /**
     * Returns total count of stored translations.
     */
    fun getTranslationCount(): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_TRANSLATIONS", null
        )
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    /**
     * Trims history to keep only the last MAX_HISTORY_COUNT records.
     * Deletes oldest entries beyond the limit.
     */
    private fun trimHistory(db: SQLiteDatabase) {
        db.execSQL(
            """
            DELETE FROM $TABLE_TRANSLATIONS 
            WHERE $COL_ID NOT IN (
                SELECT $COL_ID FROM $TABLE_TRANSLATIONS 
                ORDER BY $COL_TIMESTAMP DESC 
                LIMIT $MAX_HISTORY_COUNT
            )
            """.trimIndent()
        )
    }
}
