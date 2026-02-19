package com.voicetranslator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.voicetranslator.databinding.ActivitySplashBinding

/**
 * SplashActivity — premium animated launcher screen.
 * Displays for 2.5 seconds before launching MainActivity.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    companion object {
        private const val SPLASH_DURATION_MS = 2500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install system splash screen (Android 12+)
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate logo and title
        startAnimations()

        // Navigate to MainActivity after splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_DURATION_MS)
    }

    /**
     * Runs entrance animations on logo, title, and tagline views.
     */
    private fun startAnimations() {
        // Logo scale-in bounce animation
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.scale_in_bounce)
        binding.ivLogo.startAnimation(logoAnim)

        // Title slide up (delayed)
        val titleAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in)
        titleAnim.startOffset = 300
        binding.tvAppName.startAnimation(titleAnim)

        // Tagline slide up (more delay)
        val taglineAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in)
        taglineAnim.startOffset = 500
        binding.tvTagline.startAnimation(taglineAnim)

        // Version text fade in
        val versionAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
        versionAnim.startOffset = 800
        binding.tvVersion.startAnimation(versionAnim)
    }
}
