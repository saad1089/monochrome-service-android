# Monochrome Service

An Android Accessibility Service that toggles grayscale mode using a hardware hotkey (Volume Up + Volume Down).

## Features
- **Hotkey Trigger:** Press Volume Up and Volume Down simultaneously to trigger the grayscale prompt.
- **Timed Color:** Choose to have color for 1 or 2 hours before it automatically reverts to grayscale.
- **Minimalist:** No UI activity, just a lightweight background service.

## Installation & Setup

### 1. Build and Install
Connect your device via ADB and run:
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 2. Grant System Permissions
Since this app modifies system-level display settings, you must grant it permissions manually via ADB:
```bash
# Grant permission to write to system settings
adb shell pm grant com.example.bw android.permission.WRITE_SECURE_SETTINGS

# Allow the app to show the "How many hours of color?" dialog over other apps
adb shell appops set com.example.bw SYSTEM_ALERT_WINDOW allow
```

### 3. Activate Service
Go to **Settings > Accessibility** on your phone and turn on **Monochrome Service**.

## Configuration
The service is configured in `app/src/main/res/xml/accessibility_service_config.xml`. It listens for key events to detect the volume combination.

## Code Structure
- `MonochromeService.kt`: The core logic for key detection and toggling `accessibility_display_daltonizer_enabled`.
- `BootReceiver.kt`: Ensures the system is aware of the service on boot.
- `AndroidManifest.xml`: Defines required permissions and service declarations.
