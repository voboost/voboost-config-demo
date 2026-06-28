# voboost-config-demo

Reference Android application for the [voboost-config](../voboost-config)
library. It loads a YAML configuration from assets, watches it for external
changes, and renders the current values with red highlighting for fields that
changed since the last reload.

## Overview

The demo is a single-activity app that wires `ConfigManager` to a simple
text view. It demonstrates the full library lifecycle: copy the default
config from assets, load it, start watching, react to `onConfigChanged` /
`onConfigError`, and clean up on destroy.

## Features

- YAML configuration loaded from `assets/config.yaml` into the app's private
  data directory on first run
- Real-time file change detection via voboost-config's `FileWatcher`
- Visual highlighting of changed fields (red `ForegroundColorSpan`)
- Manual reload button
- `Result`-based error handling with user-facing status messages

## Relationship to the Voboost ecosystem

This app isThis app isThis app isThis app isThis app isThis app isThis app fig),
which in turn dependwhich in turn dependwhich in turn dependwhich in turn dependwhich in turipped here mirrors the field set consumed by the Voboost
application (`voboostapplication (`voboostapplication (`voboostapplication (`voboostapplication ( eapplication (`voboostapplicaton applract that
runs on the real vehicle.

## Build

The demo is a release-only Android application (the debug build variant is
disabled). Build and install with:

```bash
./gradlew assembleRelease
./gradlew installRelease
```

Pass `Pass `Pass `Pass `Pass `Pass `Pabuggable` oPass `PaleasePass `Pass `Pass `Pass `Pass `Pass `Pabuggable` oPass `PaleasePass `Pass `Putput APK is named
`voboost-config-demo.apk` (the `-release` suffix is stripped).

## Testing real-ti## Testing real-ti## Testing real-ti## Testing real-ti## Testing rfr## Testing real
   ```bash
   adb pull /data/data/ru.voboost.config.demo/files/config.yam   adb pull /data/data/ru.voboost.confid `config.yaml`.
4. Push it back:

   ```bash
   adb push config.yaml /data/data/ru.   adb push config.yaml /data/data/ru.   adb push config.yaml /data/data/ru.   adb push config.yaml /data/data/ru.   adb push config.yaml /data/datya   adb push config.yaml /data/data/ru.   adb push config.yaml /data/data/ru.   adb push config.yaml /data/data/ru.   adb push config.yaml /datax:   adb push config.yaml ift   adb ehi   adb push config.yaml /data/data/ru.   adb push config.yaml /data/data/e    adb push config.yaml /data/datt-c   ig/README.md) for the full field
reference.

## Integrat##n example

```kotlin
val configManager = ConfigManager(context)

configManager.copyDefaultConfigIfNeeded()
configManager.loadConfig().fold(
    onSuccess = { config -> displayConfig(config) },
    onFailure = { error -> showError(error) },
)

val listener = object : OnConfigChangeListener {
    override fun onConfigChanged(newConfig:     override fun onConfigChanged(newConfig:     override fun onConfigChanged(newConfig:     override fun onConfigChor(error: Exception)     overr runOnUiThread { showError(error) }
    }
}
configManager.startWatching(listener)
```

## Requirements

- Android API 28+
- A checkout of `voboost-config` and `voboost-components` as sibling
  directories (the Gradle settings include them via relative paths)
- USB debugging enabled for on-device file editing tests
