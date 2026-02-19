package com.voicetranslator

import android.app.Application
import com.google.android.gms.ads.MobileAds

/**
 * Application class - initializes AdMob and global configurations.
 */
class VoiceTranslatorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize AdMob SDK on a background thread (recommended by Google)
        Thread {
            MobileAds.initialize(this) { initializationStatus ->
                // AdMob initialized — log adapter statuses if needed
            }
        }.start()
    }
}
