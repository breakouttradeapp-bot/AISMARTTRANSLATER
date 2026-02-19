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

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    companion object {
        private const val SPLASH_DURATION_MS = 2500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Android 12 splash
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_DURATION_MS)
    }

    private fun startAnimations() {

        // Logo animation
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.scale_in_bounce)
        binding.logo.startAnimation(logoAnim)

        // App name animation
        val titleAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in)
        titleAnim.startOffset = 300
        binding.appName.startAnimation(titleAnim)

        // Subtitle animation
        val subAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in)
        subAnim.startOffset = 500
        binding.sub.startAnimation(subAnim)

        // Developed by animation
        val devAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
        devAnim.startOffset = 700
        binding.dev.startAnimation(devAnim)

        // Version animation
        val versionAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
        versionAnim.startOffset = 900
        binding.version.startAnimation(versionAnim)
    }
}

