package com.voicetranslator

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.voicetranslator.adapters.HistoryAdapter
import com.voicetranslator.data.Translation
import com.voicetranslator.databinding.ActivityHistoryBinding
import com.voicetranslator.viewmodel.MainViewModel

/**
 * HistoryActivity — displays saved translation history from SQLite.
 * Supports delete individual items and clear all history.
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Load history data
        viewModel.loadHistory()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Translation History"
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            onDeleteClick = { translation -> confirmDelete(translation) },
            onItemClick = { translation -> reuseTranslation(translation) }
        )

        binding.rvHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewModel.historyList.observe(this) { list ->
            historyAdapter.submitList(list)

            // Show/hide empty state
            if (list.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            }

            // Update count badge
            binding.tvCount.text = "${list.size} translations"
        }
    }

    private fun setupClickListeners() {
        // Clear all history button
        binding.btnClearAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Delete all ${historyAdapter.itemCount} translations? This cannot be undone.")
                .setPositiveButton("Clear All") { _, _ ->
                    viewModel.clearHistory()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    /**
     * Shows confirmation dialog before deleting a single translation.
     */
    private fun confirmDelete(translation: Translation) {
        AlertDialog.Builder(this)
            .setTitle("Delete Translation")
            .setMessage("Remove this translation from history?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTranslation(translation.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Returns to MainActivity with the tapped translation pre-filled.
     */
    private fun reuseTranslation(translation: Translation) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("input_text", translation.inputText)
            putExtra("translated_text", translation.translatedText)
            putExtra("source_lang", translation.sourceLang)
            putExtra("target_lang", translation.targetLang)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }
}
