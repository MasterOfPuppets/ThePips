# The Pips

A modern Android application designed to synthesize and broadcast precise time signals (pips) directly from your device. 

Historically used by radio stations (like the famous BBC Greenwich Time Signal) to allow listeners to calibrate their clocks, "The Pips" brings this classic concept to the modern era with mathematical precision, zero pre-recorded audio files, and a fully customizable schedule engine.

## 🎯 Features

*   **Real-Time Audio Synthesis:** No `.mp3` or `.wav` files are used. All sounds are generated mathematically in real-time using the `AudioTrack` API, ensuring exact frequency pitches and millisecond-accurate durations.
*   **Custom Pip Engine:** Create your own signals using the 8-slot sequencer. Define exact Hz and ms for each tone or silence.
*   **Classic Presets Included:** Comes pre-loaded with classic broadcasting signals, including the authentic BBC GTS (Greenwich Time Signal).
*   **Smart Scheduling:** Schedule your Pips to ring at the top of the hour, half-hours, or specific minutes on selected days of the week.
*   **Battery Efficient:** Built to respect your device's battery. The app remains completely asleep until the exact minute it needs to ring, utilizing Android's `AlarmManager` exact alarms.
*   **Quick Settings Tile:** Toggle the entire system ON or OFF directly from your Android notification dropdown.

## 🛠️ Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material Design 3)
*   **Architecture:** Clean Architecture + MVVM
*   **Dependency Injection:** Dagger Hilt
*   **Local Database:** Room Database
*   **Preferences:** Jetpack DataStore
*   **Background Work:** `AlarmManager` (Exact Alarms), `BroadcastReceiver`, and `TileService`
*   **Audio:** Android `AudioTrack` API (PCM 16-bit sine wave generation)

## 📖 How it Works

The architecture is divided into three main concepts:

1.  **Pip Library:** The place where you create the audio signatures. A Pip consists of up to 8 slots of Frequency/Duration pairs.
2.  **Schedules:** The rules engine. You assign a Pip to a Schedule and define the active days, hours, and minutes.
3.  **The Master Switch:** To guarantee battery efficiency, the app requires the Master Switch to be turned ON. Once active, the system calculates the next valid minute and schedules a highly optimized exact alarm to trigger the audio synthesis.

## 🚀 Installation

You can download the latest stable APK from the [Releases](../../releases) page.

*(Note: On Android 12+, the app requires the "Alarms & Reminders" permission to schedule exact time signals. The app will prompt you automatically when turning on the Master Switch).*

---
Made with ❤️ by MasterOfPuppets and Gemini.