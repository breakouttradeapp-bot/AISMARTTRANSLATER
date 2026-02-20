package com.niv.aivoicetranslator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.niv.aivoicetranslator.data.Translation
import com.niv.aivoicetranslator.databinding.ItemHistoryBinding
import com.niv.aivoicetranslator.utils.LanguageUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for displaying translation history.
 * Uses ListAdapter with DiffUtil for efficient updates.
 */
class HistoryAdapter(
    private val onDeleteClick: (Translation) -> Unit,
    private val onItemClick: (Translation) -> Unit
) : ListAdapter<Translation, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Translation>() {
            override fun areItemsTheSame(old: Translation, new: Translation) = old.id == new.id
            override fun areContentsTheSame(old: Translation, new: Translation) = old == new
        }

        private val DATE_FORMAT = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(translation: Translation) {
            binding.apply {
                // Source text (truncated for UI)
                tvInputText.text = translation.inputText
                tvTranslatedText.text = translation.translatedText

                // Language pair display: EN → ES
                tvLanguagePair.text = buildString {
                    append(translation.sourceLang.uppercase())
                    append("  →  ")
                    append(translation.targetLang.uppercase())
                }

                // Human-readable timestamp
                tvTimestamp.text = DATE_FORMAT.format(Date(translation.timestamp))

                // Delete button
                btnDelete.setOnClickListener { onDeleteClick(translation) }

                // Tap card to re-use translation
                root.setOnClickListener { onItemClick(translation) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
