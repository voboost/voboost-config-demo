# Technical Context: voboost-config-demo

## Technology Stack

### Core Technologies
- **Kotlin** - Primary development language for Android
- **Android SDK** - Target platform (API 28+)
- **voboost-config Library** - Configuration management dependency
- **Gradle 8.14.1** - Build system with Kotlin DSL

### Android Framework
- **Activity** - Main application component (MainActivity)
- **Context** - File system access and resource management
- **UI Components** - TextView for configuration display
- **Asset Manager** - Default configuration file handling

### Development Tools
- **Android Studio** - Primary IDE
- **Git** - Version control
- **Gradle Wrapper** - Build tool consistency

## Project Structure

### Application Structure
```
voboost-config-demo/
├── src/main/
│   ├── java/ru/voboost/config/demo/
│   │   └── MainActivity.kt          # Main demonstration activity
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    # Main UI layout
│   │   ├── values/
│   │   │   ├── colors.xml           # Color definitions
│   │   │   ├── strings.xml          # String resources
│   │   │   └── themes.xml           # App themes
│   │   └── drawable/
│   │       └── config_text_background.xml  # Background styling
│   └── assets/
│       └── config.yaml              # Default configuration file
├── build.gradle.kts                 # Application build configuration
└── memory-bank/                     # Project documentation
```

### Package Organization
```
ru.voboost.config.demo/
└── MainActivity.kt                  # Main application activity
```

## Dependencies

### Library Dependencies
```kotlin
// Main library dependency
implementation(project(":voboost-config"))
// For external usage: implementation("ru.voboost:voboost-config:1.0.0")

// Android core dependencies
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
```

### Transitive Dependencies
Through voboost-config library:
- **Hoplite Core** - YAML parsing
- **Hoplite YAML** - YAML format support
- **Hoplite Watch** - File watching capabilities
- **Kotlinx Coroutines** - Async operations

## Build Configuration

### Android Application Setup
```kotlin
android {
    namespace = "ru.voboost.config.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.voboost.config.demo"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
```

### Gradle Configuration
- **Kotlin DSL** - Modern build script format
- **Android Gradle Plugin 8.2.0** - Latest stable version
- **Kotlin 1.9.20** - Compatible with library version
- **Minimum API 28** - Android 9.0 and above

## File System Integration

### Asset Management
```kotlin
// Copy configuration from assets to private directory
private fun copyConfigFromAssets() {
    val configFile = File(filesDir, "config.yaml")
    if (!configFile.exists()) {
        assets.open("config.yaml").use { input ->
            configFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
```

### File Operations
- **Context.filesDir** - Private app directory for configuration files
- **Asset Integration** - Default configuration in app assets
- **File Copying** - Automatic asset-to-files copying on first run
- **File Watching** - Real-time monitoring through library

## Configuration Format

### YAML Structure
```yaml
settings:
  language: "ru"              # Language enum: ru, en
  theme: "dark"               # Theme enum: auto, light, dark
  interface-shift-x: 0        # Integer positioning
  interface-shift-y: 0        # Interface positioning

vehicle:
  fuel-mode: "electric"       # FuelMode enum: intellectual, electric, fuel, save
  drive-mode: "sport"         # DriveMode enum: eco, comfort, sport, snow, outing, individual
```

### Data Model Integration
- **Config Class** - Root configuration object from library
- **Settings Section** - User interface preferences
- **Vehicle Section** - Vehicle-specific configuration
- **Enum Mapping** - Automatic string-to-enum conversion
- **Nullable Fields** - Graceful handling of missing values

## UI Implementation

### Layout Design
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/configText"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@drawable/config_text_background"
        android:padding="16dp"
        android:textSize="16sp"
        android:fontFamily="monospace" />

    <TextView
        android:id="@+id/statusText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:textSize="14sp" />

</LinearLayout>
```

### Visual Styling
- **Monospace Font** - Clear configuration value display
- **Background Styling** - Professional appearance with rounded corners
- **Color Scheme** - Black text with red highlighting for changes
- **Responsive Layout** - Adapts to different screen sizes

## Real-time Update Implementation

### Configuration Loading
```kotlin
// Actual implementation from MainActivity.kt
private fun loadAndDisplayConfig() {
    val result = configManager.loadConfig(this, configFileName)

    result.fold(
        onSuccess = { config ->
            Log.d("MainActivity", "Configuration loaded successfully: $config")
            displayConfig(config)
            showToast("Configuration loaded successfully")
        },
        onFailure = { error ->
            Log.e("MainActivity", "Failed to load configuration", error)
            configTextView.text = "Error loading configuration:\n${error.message}"
            showToast("Failed to load configuration")
        }
    )
}
```

### Change Detection
```kotlin
// MainActivity implements OnConfigChangeListener directly
override fun onConfigChanged(newConfig: Config, diff: Config) {
    runOnUiThread {
        displayConfig(newConfig, diff)
    }
}
```

### File Watching Setup
```kotlin
// Actual implementation from MainActivity.kt
private fun startWatchingConfig() {
    val result = configManager.startWatching(this, configFileName, this)

    result.fold(
        onSuccess = {
            showToast("Started watching configuration file")
        },
        onFailure = { error ->
            showToast("Failed to start watching: ${error.message}")
        }
    )
}
```

## Visual Feedback System

### Red Color Highlighting
```kotlin
// Actual implementation using reflection and field paths
private fun displayConfig(config: Config, diff: Config? = null) {
    val spannableText = SpannableStringBuilder()

    spannableText.appendLine("=== VOBOOST CONFIG DEMO ===")
    spannableText.appendLine()

    spannableText.appendLine("SETTINGS:")
    appendConfigLine(config, diff, spannableText, "settings.language")
    appendConfigLine(config, diff, spannableText, "settings.theme")
    appendConfigLine(config, diff, spannableText, "settings.interfaceShiftX")
    appendConfigLine(config, diff, spannableText, "settings.interfaceShiftY")
    spannableText.appendLine()

    spannableText.appendLine("VEHICLE:")
    appendConfigLine(config, diff, spannableText, "vehicle.fuelMode")
    appendConfigLine(config, diff, spannableText, "vehicle.driveMode")
    spannableText.appendLine()

    spannableText.appendLine("Configuration file location:")
    spannableText.appendLine("${File(filesDir, configFileName).absolutePath}")
    spannableText.appendLine()
    spannableText.appendLine("You can modify the config.yaml file and see changes in real-time!")

    configTextView.text = spannableText
}

private fun appendConfigLine(
    config: Config,
    diff: Config?,
    spannableText: SpannableStringBuilder,
    fieldPath: String
) {
    val value = getFieldValue(config, fieldPath)
    val line = "  $fieldPath: ${value ?: "Not set"}"
    val startIndex = spannableText.length
    spannableText.appendLine(line)

    val isChanged = isFieldChanged(diff, fieldPath)
    val color = if (isChanged) {
        ContextCompat.getColor(this, R.color.config_changed_text) // Red (#D32F2F)
    } else {
        ContextCompat.getColor(this, android.R.color.black) // Black
    }

    spannableText.setSpan(
        ForegroundColorSpan(color),
        startIndex,
        spannableText.length - 1, // -1 to exclude newline
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}
```

### Dynamic Text Building
- **SpannableStringBuilder** - Efficient text formatting
- **ForegroundColorSpan** - Red color application
- **Conditional Logic** - Only highlight actual changes
- **Scalable Design** - Easy addition of new configuration fields

## Error Handling Strategy

### Result Pattern Usage
```kotlin
// Consistent error handling throughout application
configManager.loadConfig(this, "config.yaml")
    .onSuccess { config ->
        // Handle successful configuration loading
        displayConfiguration(config)
    }
    .onFailure { error ->
        // Handle errors gracefully
        showErrorMessage(error.message ?: "Unknown error")
    }
```

### Error Categories
- **File Not Found** - Configuration file doesn't exist
- **Parse Errors** - Invalid YAML syntax from library
- **Permission Errors** - File system access problems
- **Library Errors** - Configuration loading failures

### Error Presentation
- **Status Display** - Clear error messages in UI
- **Color Coding** - Red text for errors, green for success
- **User-friendly Messages** - Descriptive error descriptions
- **Graceful Degradation** - App continues working despite errors

## Performance Characteristics

### Memory Usage
- **Minimal State** - Activity holds only essential references
- **Efficient UI Updates** - Only rebuild when configuration changes
- **Resource Cleanup** - Proper disposal of file watchers in onDestroy()
- **Garbage Collection** - Avoid memory leaks in long-running operations

### UI Performance
- **Background Operations** - File watching off main thread
- **UI Thread Safety** - All UI updates on main thread via runOnUiThread
- **Lazy Updates** - Only update display when changes occur
- **Efficient Text Building** - Reuse SpannableStringBuilder when possible

## Development Workflow

### Build Commands
```bash
# Build demo application
./gradlew assembleDebug

# Install on device/emulator
./gradlew installDebug

# Run application
adb shell am start -n ru.voboost.config.demo/.MainActivity

# View logs
adb logcat | grep "voboost"
```

### Testing Commands
```bash
# Run unit tests (if any)
./gradlew testDebugUnitTest

# Run instrumented tests (if any)
./gradlew connectedDebugAndroidTest
```

## Deployment Considerations

### Application Distribution
- **APK Format** - Standard Android application package
- **Play Store Compatible** - Meets all distribution requirements
- **Minimum API 28** - Android 9.0 and above
- **No Special Permissions** - Uses only standard app permissions

### Installation Requirements
- **Android Device** - API 28+ (Android 9.0)
- **Storage Space** - Minimal space requirements
- **No Network** - Works completely offline
- **No Special Setup** - Ready to run after installation

## Code Quality Standards

### Coding Conventions
- **English Language** - All code, comments, and documentation
- **Blank Line Endings** - All source files end with empty line
- **Clear Naming** - Descriptive variable and method names
- **Consistent Formatting** - Standard Kotlin code style

### Quality Metrics
- **Clean Compilation** - No warnings or errors
- **Resource Management** - Proper cleanup of Android resources
- **Thread Safety** - Correct handling of UI thread operations
- **Error Handling** - Comprehensive error management

This technical foundation provides a robust, maintainable demonstration application that showcases the voboost-config library capabilities in a real Android environment.