package com.voicetranslator

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.voicetranslator.databinding.ActivityMainBinding
import com.voicetranslator.utils.AdManager
import com.voicetranslator.utils.LanguageUtils
import com.voicetranslator.utils.NetworkUtils
import com.voicetranslator.utils.PrefsManager
import com.voicetranslator.viewmodel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var prefs: PrefsManager
    private lateinit var adManager: AdManager

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var speechRecognizer: SpeechRecognizer? = null

    companion object {
        private const val BANNER_AD_UNIT_ID =
            "ca-app-pub-3940256099942544/6300978111"
    }

    private val audioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startVoiceInput()
            else showError("Microphone permission required.")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsManager(this)
        adManager = AdManager(this)

        setupToolbar()
        setupSpinners()
        setupClickListeners()
        setupBannerAd()
        observeViewModel()

        tts = TextToSpeech(this, this)
        adManager.loadInterstitialAd()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ✅ SPINNER SETUP WITH DEFAULT HINDI → ENGLISH
    private fun setupSpinners() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            LanguageUtils.displayNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerSource.adapter = adapter
        binding.spinnerTarget.adapter = adapter

        // Default Hindi → English
        binding.spinnerSource.setSelection(LanguageUtils.getIndexByCode("hi"))
        binding.spinnerTarget.setSelection(LanguageUtils.getIndexByCode("en"))

        prefs.sourceLang = "hi"
        prefs.targetLang = "en"

        binding.spinnerSource.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prefs.sourceLang = LanguageUtils.languages[position].second
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.spinnerTarget.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prefs.targetLang = LanguageUtils.languages[position].second
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // ✅ Swap Button Working
        binding.btnSwap.setOnClickListener {
            val src = binding.spinnerSource.selectedItemPosition
            val tgt = binding.spinnerTarget.selectedItemPosition

            binding.spinnerSource.setSelection(tgt)
            binding.spinnerTarget.setSelection(src)

            val input = binding.etInputText.text.toString()
            val output = binding.tvTranslatedText.text.toString()

            binding.etInputText.setText(output)
            binding.tvTranslatedText.text = input
        }
    }

    private fun setupClickListeners() {

        binding.btnTranslate.setOnClickListener {
            val text = binding.etInputText.text.toString().trim()

            if (!NetworkUtils.isConnected(this)) {
                showError("No internet connection")
                return@setOnClickListener
            }

            if (text.isBlank()) {
                binding.etInputText.error = "Enter text"
                return@setOnClickListener
            }

            viewModel.translate(text, prefs.sourceLang, prefs.targetLang)
        }

        binding.btnMic.setOnClickListener {
            checkMicrophonePermission()
        }

        binding.btnSpeak.setOnClickListener {
            val text = binding.tvTranslatedText.text.toString()
            if (text.isNotBlank()) {
                speakText(text, prefs.targetLang)
            }
        }

        binding.btnCopy.setOnClickListener {
            val text = binding.tvTranslatedText.text.toString()
            if (text.isNotBlank()) {
                val clipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(
                    ClipData.newPlainText("Translation", text)
                )
                Snackbar.make(binding.root, "Copied", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.translationState.observe(this) { state ->
            when (state) {
                is MainViewModel.TranslationState.Loading ->
                    binding.progressBar.visibility = View.VISIBLE

                is MainViewModel.TranslationState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvTranslatedText.text = state.translatedText
                }

                is MainViewModel.TranslationState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showError(state.message)
                }

                else -> binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startVoiceInput()
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // ✅ FULLY WORKING MIC
    private fun startVoiceInput() {

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            showError("Speech recognition not available")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.sourceLang)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.getOrNull(0)

                if (!text.isNullOrEmpty()) {
                    binding.etInputText.setText(text)
                }
            }

            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity,
                    "Mic error",
                    Toast.LENGTH_SHORT).show()
            }

            override fun onReadyForSpeech(p0: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    override fun onInit(status: Int) {
        ttsReady = status == TextToSpeech.SUCCESS
    }

    private fun speakText(text: String, langCode: String) {
        if (!ttsReady) return

        val locale = Locale.forLanguageTag(langCode)
        tts?.setLanguage(locale)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts")
    }

    private fun setupBannerAd() {
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = BANNER_AD_UNIT_ID
        binding.adContainer.addView(adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroy() {
        tts?.shutdown()
        speechRecognizer?.destroy()
        super.onDestroy()
    }
}

