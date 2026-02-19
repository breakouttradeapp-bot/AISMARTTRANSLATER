package com.voicetranslator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.voicetranslator.api.TranslationRepository
import com.voicetranslator.data.Translation
import kotlinx.coroutines.launch

/**
 * ViewModel for MainActivity.
 * Manages translation state using LiveData and coroutines.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TranslationRepository(application)

    // Translation state
    private val _translationState = MutableLiveData<TranslationState>()
    val translationState: LiveData<TranslationState> = _translationState

    // History list
    private val _historyList = MutableLiveData<List<Translation>>()
    val historyList: LiveData<List<Translation>> = _historyList

    /**
     * Sealed class representing translation UI states.
     */
    sealed class TranslationState {
        object Loading : TranslationState()
        data class Success(val translatedText: String) : TranslationState()
        data class Error(val message: String) : TranslationState()
        object Idle : TranslationState()
    }

    /**
     * Initiates a translation request on a background coroutine.
     *
     * @param text Source text to translate.
     * @param sourceLang Source language code.
     * @param targetLang Target language code.
     */
    fun translate(text: String, sourceLang: String, targetLang: String) {
        if (text.isBlank()) {
            _translationState.value = TranslationState.Error("Please enter text to translate.")
            return
        }

        viewModelScope.launch {
            _translationState.value = TranslationState.Loading

            val result = repository.translate(text, sourceLang, targetLang)

            _translationState.value = when (result) {
                is TranslationRepository.TranslationResult.Success ->
                    TranslationState.Success(result.translatedText)
                is TranslationRepository.TranslationResult.Error ->
                    TranslationState.Error(result.message)
            }
        }
    }

    /**
     * Loads translation history from SQLite.
     */
    fun loadHistory() {
        viewModelScope.launch {
            _historyList.value = repository.getHistory()
        }
    }

    /**
     * Deletes a single translation record.
     */
    fun deleteTranslation(id: Long) {
        repository.deleteTranslation(id)
        loadHistory()
    }

    /**
     * Clears all translation history.
     */
    fun clearHistory() {
        repository.clearHistory()
        loadHistory()
    }

    /**
     * Resets state to Idle (e.g., after clearing the input).
     */
    fun resetState() {
        _translationState.value = TranslationState.Idle
    }
}
