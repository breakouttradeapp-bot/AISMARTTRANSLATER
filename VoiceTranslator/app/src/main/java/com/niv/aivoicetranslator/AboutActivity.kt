package com.niv.aivoicetranslator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.niv.aivoicetranslator.databinding.ActivityAboutBinding

/**
 * AboutActivity — displays app info, credits, and legal links.
 */
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupContent()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "About"
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupContent() {
        binding.tvVersion.text = "Version ${BuildConfig.VERSION_NAME}"
    }

    private fun setupClickListeners() {

        // Privacy Policy
        binding.tvPrivacyPolicy.setOnClickListener {
            openUrl("https://your-website.com/privacy-policy")
        }

        // Terms of Service
        binding.tvTerms.setOnClickListener {
            openUrl("https://your-website.com/terms")
        }

        // Rate App
        binding.btnRateApp.setOnClickListener {
            openUrl("https://play.google.com/store/apps/details?id=${packageName}")
        }

        // Share App
        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Voice Translator App")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Try this amazing AI Voice Translator app!\n" +
                    "Download: https://play.google.com/store/apps/details?id=$packageName"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Share App"))
        }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
