# voboost-config-demo

Android demo application showcasing [voboost-config](../voboost-config) library integration and real-time configuration monitoring.

## What it does

- Loads YAML configuration from assets, copies to app's private directory
- Displays configuration values in a scrollable text view
- Monitors configuration file changes in real-time
- Highlights changed fields in red color when file is modified externally
- Provides manual reload functionality

## Key components

- [`MainActivity.kt`](src/main/java/ru/voboost/config/demo/MainActivity.kt) - Main activity implementing `OnConfigChangeListener`
- [`config.yaml`](src/main/assets/config.yaml) - Default configuration with sample values
- Real-time file watching through voboost-config library
- Visual feedback using `SpannableStringBuilder` with color highlighting

## Configuration structure

```yaml
# Language: ru, en
settings-language: en

# Theme: light, dark
settings-theme: dark

# Interface positioning
settings-interface-shift-x: 0
settings-interface-shift-y: 0

# Vehicle settings
vehicle-fuel-mode: electric    # electric, hybrid, save
vehicle-drive-mode: individual # eco, comfort, sport, snow, outing, individual
```

## How to test real-time updates

1. Install and run the app
2. Use Android Studio Device File Explorer to navigate to:
   `/data/data/ru.voboost.config.demo/config.yaml`
3. Download the file, edit values, upload back
4. Watch the app immediately highlight changed fields in red

Alternative: Use ADB commands
```bash
adb pull /data/data/ru.voboost.config.demo/config.yaml
# Edit the file
adb push config.yaml /data/data/ru.voboost.config.demo/config.yaml
```

## Build and run

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Requirements

- Android API 28+
- voboost-config library dependency (automatically included)
- USB debugging enabled for file access testing

## Technical details

- Uses `ConfigManager.initialize()` with singleton pattern
- Implements `OnConfigChangeListener` for real-time updates
- File operations through Android `Context.dataDir`
- UI updates on main thread via `runOnUiThread`
- Proper resource cleanup in activity lifecycle
- Error handling with Result pattern from library
