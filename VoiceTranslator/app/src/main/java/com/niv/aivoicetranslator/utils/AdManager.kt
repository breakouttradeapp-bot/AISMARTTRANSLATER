package com.niv.aivoicetranslator.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Manages AdMob interstitial ad loading and display.
 * Shows interstitial every 3 translations.
 */
class AdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private val TAG = "AdManager"

    companion object {
        // Test interstitial ID — replace with real ID for production
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val TRANSLATIONS_PER_AD = 3
    }

    /**
     * Preloads the interstitial ad so it's ready to display instantly.
     * Should be called on app start and after each ad display.
     */
    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial ad loaded successfully.")

                    // Set callback for when ad is dismissed
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            // Reload for next opportunity
                            loadInterstitialAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            interstitialAd = null
                            loadInterstitialAd()
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.e(TAG, "Interstitial ad failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows the interstitial ad if loaded and translation count threshold is met.
     * Call this after each successful translation.
     *
     * @param activity The current Activity context.
     * @param prefs PrefsManager to track and reset translation count.
     */
    fun showInterstitialIfReady(activity: Activity, prefs: PrefsManager) {
        prefs.incrementTranslationCount()

        if (prefs.translationCount >= TRANSLATIONS_PER_AD) {
            if (interstitialAd != null) {
                interstitialAd?.show(activity)
                prefs.resetTranslationCount()
            } else {
                // Ad not ready yet, reset count and reload
                prefs.resetTranslationCount()
                loadInterstitialAd()
            }
        }
    }
}
