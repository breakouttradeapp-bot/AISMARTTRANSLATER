# 🌐 Voice Translator — Production Android App

An AI-powered voice translation app for Android built with Kotlin, Material Design 3, and the Deep Translate API via RapidAPI.

---

## ✨ Features

| Feature | Details |
|---------|---------|
| 🎙️ Voice Input | Speech-to-Text using Android SpeechRecognizer |
| 🌍 Translation | Deep Translate API via RapidAPI (100+ languages) |
| 🔊 TTS Output | Text-to-Speech with adjustable speed |
| 💾 History | SQLite database — last 50 translations |
| 🌙 Dark/Light Theme | Material Design 3 with full theming |
| 📢 AdMob | Banner + Interstitial ads (every 3 translations) |
| ✨ Premium UI | Gradient toolbar, smooth animations, modern cards |

---

## 🚀 Setup Instructions

### 1. Clone / Open in Android Studio

```
File → Open → Select VoiceTranslator folder
```

### 2. Configure API Key

1. Go to [RapidAPI.com](https://rapidapi.com)
2. Search for **"Deep Translate"** and subscribe (free tier available)
3. Copy your API key
4. Open `app/build.gradle`
5. Replace `YOUR_RAPIDAPI_KEY_HERE` with your actual key:

```gradle
buildConfigField "String", "RAPID_API_KEY", '"your_actual_key_here"'
```

### 3. Configure AdMob (for production)

1. Create account at [AdMob](https://admob.google.com)
2. Create an app and get your App ID + Ad Unit IDs
3. Update `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="YOUR_ADMOB_APP_ID" />
```
4. Update Ad Unit IDs in:
   - `MainActivity.kt` → `BANNER_AD_UNIT_ID`
   - `utils/AdManager.kt` → `INTERSTITIAL_AD_UNIT_ID`

> **Note:** Test ad IDs are already configured. Your app will show test ads without any setup.

### 4. Add Poppins Fonts (Optional)

Download from [Google Fonts](https://fonts.google.com/specimen/Poppins) and add:
- `poppins_regular.ttf` → `app/src/main/res/font/`
- `poppins_bold.ttf` → `app/src/main/res/font/`

### 5. Build & Run

```
Build → Make Project (Ctrl+F9)
Run → Run 'app' (Shift+F10)
```

---

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/voicetranslator/
│   │   ├── VoiceTranslatorApp.kt       # Application class (AdMob init)
│   │   ├── SplashActivity.kt           # Splash screen
│   │   ├── MainActivity.kt             # Main translator screen
│   │   ├── HistoryActivity.kt          # Translation history
│   │   ├── SettingsActivity.kt         # App settings
│   │   ├── AboutActivity.kt            # About screen
│   │   ├── api/
│   │   │   ├── TranslationApiService.kt  # Retrofit + API models
│   │   │   └── TranslationRepository.kt  # Repository pattern
│   │   ├── data/
│   │   │   ├── Translation.kt          # Data model
│   │   │   └── DatabaseHelper.kt       # SQLite helper
│   │   ├── adapters/
│   │   │   └── HistoryAdapter.kt       # RecyclerView adapter
│   │   ├── viewmodel/
│   │   │   └── MainViewModel.kt        # ViewModel (MVVM)
│   │   └── utils/
│   │       ├── NetworkUtils.kt         # Internet check
│   │       ├── PrefsManager.kt         # SharedPreferences
│   │       ├── LanguageUtils.kt        # 100+ language codes
│   │       └── AdManager.kt            # AdMob interstitial
│   └── res/
│       ├── layout/                     # XML layouts
│       ├── drawable/                   # Icons, gradients
│       ├── values/                     # Colors, strings, themes
│       ├── values-night/               # Dark theme
│       ├── anim/                       # Animations
│       ├── menu/                       # Toolbar menus
│       └── font/                       # Poppins fonts
```

---

## 🏗️ Architecture

- **Pattern:** MVVM (Model-View-ViewModel)
- **Language:** Kotlin
- **UI:** Material Design 3
- **Networking:** Retrofit 2 + OkHttp 4
- **Async:** Kotlin Coroutines
- **Database:** SQLite via DatabaseHelper
- **DI:** Manual (no Hilt/Dagger for simplicity)

---

## 📲 Play Store Checklist

- [ ] Replace test AdMob IDs with production IDs
- [ ] Add your RapidAPI key
- [ ] Add Poppins font files to `res/font/`
- [ ] Update privacy policy URL in `AboutActivity.kt`
- [ ] Update Play Store URL in `AboutActivity.kt`
- [ ] Create app signing keystore
- [ ] Generate release APK/AAB: `Build → Generate Signed Bundle / APK`
- [ ] Test on multiple devices/APIs
- [ ] Add app screenshots for Play Store listing

---

## 🔑 API Reference

**Endpoint:** `POST https://deep-translate1.p.rapidapi.com/language/translate/v2`

```json
{
  "q": "Hello World",
  "source": "en",
  "target": "es"
}
```

**Response:**
```json
{
  "data": {
    "translations": {
      "translatedText": "Hola Mundo"
    }
  }
}
```

---

## 📄 License

MIT License — Free to use and modify for commercial and personal projects.

---

*Built with ❤ using Kotlin & Material Design 3*
