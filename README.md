# Voboost Config Demo

**English** | [Русский](README.ru.md)

Single-activity Android demo that integrates the
[`voboost-config`](../voboost-config) library. It loads a `config.yaml` from
assets, displays the parsed configuration, and reacts to live file changes via
`ConfigManager`'s file watcher.

Part of the Voboost ecosystem; depends on [`voboost-config`](../voboost-config)
(which in turn depends on [`voboost-components`](../voboost-components)).

## What it demonstrates

- Copying a default `config.yaml` from assets to the app's data directory on first run
- Loading and displaying the flat `Config` model
- Starting `ConfigManager` file watching and receiving change diffs
- Reloading the configuration on demand
- Rendering a configuration error with the expected field format

## config.yaml

The demo ships a default config at
[`src/main/assets/config.yaml`](src/main/assets/config.yaml). It uses the flat
schema expected by the `Config` model:

```yaml
settings-language: en
settings-theme: free-dark
settings-interface-shift-x: 0
settings-interface-shift-y: 0
settings-active-tab: interface
vehicle-fuel-mode: electric
vehicle-drive-mode: comfort
interface-keyboard: enable-russian
interface-widget-weather: enable-non-chineese-cities
settings-startup: off
settings-car-model: free
vehicle-pedestrian-warning: original
```

See [`Config.kt`](../voboost-config/src/main/java/ru/voboost/config/models/Config.kt)
for the full list of fields and enum values.

## Integration example

```kotlin
class MainActivity : AppCompatActivity(), OnConfigChangeListener {
    private val configManager = ConfigManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configManager.copyDefaultConfigIfNeeded()
        configManager.loadConfig()
        configManager.startWatching(this)
    }

    override fun onConfigChanged(newConfig: Config, diff: Config) {
        if (configManager.isFieldChanged(diff, "settingsTheme")) {
            // apply new theme
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        configManager.stopWatching()
    }
}
```

## Build

- Kotlin `2.0.21`, Android Gradle Plugin `8.7.3`
- `compileSdk`/`targetSdk` 34, `minSdk` 28, Java/JVM target 11
- Release-only: the debug variant is disabled via `androidComponents.beforeVariants`,
  so `./gradlew build` produces a single release APK (signed with the debug keystore).
  `-Pdebuggable=true` flips the release variant's `isDebuggable` flag for deep
  debugging. The `-release` suffix is stripped from the output APK name.
- Unit tests run on the release variant; the `testUnit` task aliases
  `testReleaseUnitTest`.
- Code style: ktlint, Checkstyle, and Spotless, applied via
  [`voboost-codestyle`](../voboost-codestyle).

```sh
./gradlew build      # assemble release APK + run unit tests
./gradlew testUnit   # unit tests only
./gradlew lintFix     # ktlint + spotless formatting
```

## Ecosystem

- [`voboost-config`](../voboost-config) - the configuration library this demo uses
- [`voboost-components`](../voboost-components) - shared UI components
- [`voboost-codestyle`](../voboost-codestyle) - shared Gradle/code-style configuration

## License

Dual-licensed:

- [PolyForm Noncommercial 1.0.0](https://github.com/voboost/voboost-license/blob/main/LICENSE) — free for personal use
- [Commercial license](https://github.com/voboost/voboost-license/blob/main/COMMERCIAL.md) — required otherwise
