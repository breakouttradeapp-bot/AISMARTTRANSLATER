# ── VoiceTranslator ProGuard Rules ─────────────────────────────────────────

# Keep Retrofit interfaces and models
-keep class com.voicetranslator.api.** { *; }
-keep class com.voicetranslator.data.** { *; }

# Retrofit & OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# Gson
-keepattributes SerializedName
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# AdMob
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Material Design
-keep class com.google.android.material.** { *; }

# Keep BuildConfig
-keep class com.voicetranslator.BuildConfig { *; }

# Suppress warnings
-dontwarn java.lang.invoke.*
-dontwarn org.codehaus.mojo.**
