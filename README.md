# Voboost Config Demo

A comprehensive demonstration Android application that showcases the functionality of the `voboost-config` library. This demo provides real-time visual feedback for configuration changes with red color highlighting and professional user interface.

## Features

- **Real-time Configuration Display**: Shows current configuration values with professional formatting
- **Visual Change Highlighting**: Changed fields appear in red color, unchanged in black
- **File System Integration**: Demonstrates Android asset and private storage operations
- **Manual Reload**: Button to manually reload configuration for testing
- **Error Handling**: Robust error management with user-friendly messages
- **Production Quality**: Professional Android application demonstrating best practices

## Quick Start

1. **Build and Install**:
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

2. **Launch Application**: The app automatically loads configuration from assets and starts file watching

3. **View Configuration**: See current configuration values displayed with clear formatting

4. **Test Real-time Updates**: Modify the configuration file and watch immediate visual feedback

## Application Structure

### Main Components

- **MainActivity**: Single activity implementing `OnConfigChangeListener`
- **Configuration Display**: Scrollable text view with monospace font
- **Reload Button**: Manual configuration reload functionality
- **Toast Notifications**: User feedback for operations
- **Real-time Monitoring**: Automatic file change detection

### UI Layout

```xml
<LinearLayout>
    <!-- App Header -->
    <TextView android:text="Voboost Config Demo" />

    <!-- Manual Reload Button -->
    <Button android:id="@+id/reloadButton" />

    <!-- Scrollable Configuration Display -->
    <ScrollView>
        <TextView android:id="@+id/configTextView"
                  android:fontFamily="monospace" />
    </ScrollView>

    <!-- Instructions -->
    <TextView android:text="Modify config.yaml to see real-time changes" />
</LinearLayout>
```

## Configuration File Location

The application copies the default configuration from assets to:
```
/data/data/ru.voboost.config.demo/files/config.yaml
```

## Testing Real-time Updates

### Method 1: Android Studio Device File Explorer

1. **Open Device File Explorer** in Android Studio
2. **Navigate to**: `/data/data/ru.voboost.config.demo/files/config.yaml`
3. **Download the file** (right-click → Save As...)
4. **Edit the configuration**:
   ```yaml
   settings:
     language: "ru"        # Change from "en" to "ru"
     theme: "dark"         # Change theme
     interface-shift-x: 10 # Change positioning
     interface-shift-y: 5

   vehicle:
     fuel-mode: "electric" # Change fuel mode
     drive-mode: "sport"   # Change drive mode
   ```
5. **Upload modified file** (right-click → Upload...)
6. **Observe immediate changes** - modified fields appear in red color

### Method 2: ADB Commands

```bash
# Pull current configuration
adb pull /data/data/ru.voboost.config.demo/files/config.yaml

# Edit the file locally, then push back
adb push config.yaml /data/data/ru.voboost.config.demo/files/config.yaml
```

## Configuration Structure

### Settings Section
```yaml
settings:
  language: "ru"          # Language: ru, en
  theme: "dark"           # Theme: auto, light, dark
  interface-shift-x: 0    # Horizontal offset (integer)
  interface-shift-y: 0    # Vertical offset (integer)
```

### Vehicle Section
```yaml
vehicle:
  fuel-mode: "electric"   # Fuel modes: intellectual, electric, fuel, save
  drive-mode: "sport"     # Drive modes: eco, comfort, sport, snow, outing, individual
```

## Visual Feedback System

### Color Highlighting
- **Red Text (#D32F2F)**: Fields that changed in the last update
- **Black Text**: Unchanged fields
- **Immediate Updates**: Changes appear instantly when file is modified

### Display Format
```
=== VOBOOST CONFIG DEMO ===

SETTINGS:
  settings.language: ru          ← Red if changed
  settings.theme: dark           ← Red if changed
  settings.interfaceShiftX: 0    ← Black if unchanged
  settings.interfaceShiftY: 0    ← Black if unchanged

VEHICLE:
  vehicle.fuelMode: electric     ← Red if changed
  vehicle.driveMode: sport       ← Red if changed

Configuration file location:
/data/data/ru.voboost.config.demo/files/config.yaml

You can modify the config.yaml file and see changes in real-time!
```

## Library Integration Example

This demo shows how to integrate the voboost-config library:

### Basic Setup
```kotlin
class MainActivity : AppCompatActivity(), OnConfigChangeListener {
    private val configManager = ConfigManager()
    private val configFileName = "config.yaml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        copyDefaultConfigIfNeeded()
        loadAndDisplayConfig()
        startWatchingConfig()
    }
}
```

### Configuration Loading
```kotlin
private fun loadAndDisplayConfig() {
    val result = configManager.loadConfig(this, configFileName)

    result.fold(
        onSuccess = { config ->
            displayConfig(config)
            showToast("Configuration loaded successfully")
        },
        onFailure = { error ->
            configTextView.text = "Error loading configuration:\n${error.message}"
            showToast("Failed to load configuration")
        }
    )
}
```

### Real-time Change Detection
```kotlin
override fun onConfigChanged(newConfig: Config, diff: Config) {
    runOnUiThread {
        displayConfig(newConfig, diff)
    }
}

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

### Visual Highlighting Implementation
```kotlin
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
        ContextCompat.getColor(this, R.color.config_changed_text) // Red
    } else {
        ContextCompat.getColor(this, android.R.color.black) // Black
    }

    spannableText.setSpan(
        ForegroundColorSpan(color),
        startIndex,
        spannableText.length - 1,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}
```

### Resource Cleanup
```kotlin
override fun onDestroy() {
    super.onDestroy()
    configManager.stopWatching()
}
```

## Error Testing Scenarios

### Invalid YAML Syntax
Create malformed YAML to test error handling:
```yaml
settings:
  language: "en"
  theme: "auto"
# Missing proper structure
vehicle
  fuel-mode: "electric"
```

### Invalid Enum Values
Test with invalid enum values:
```yaml
settings:
  language: "spanish"     # Invalid language
  theme: "rainbow"        # Invalid theme

vehicle:
  fuel-mode: "nuclear"    # Invalid fuel mode
  drive-mode: "flying"    # Invalid drive mode
```

## Troubleshooting

### Common Issues

**Configuration not updating:**
- Check file permissions in Device File Explorer
- Verify file was uploaded correctly
- Try manual reload button
- Check logcat for error messages

**File access denied:**
- Use Android Studio Device File Explorer
- Ensure USB debugging is enabled
- Don't use third-party file managers

**App crashes:**
- Check logcat for detailed error information
- Verify YAML syntax is correct
- Restore original configuration if needed

### Debug Tips

1. **Monitor Logcat**: Filter by `ru.voboost.config.demo` for detailed logs
2. **Use Manual Reload**: Test configuration loading without file watching
3. **Start Simple**: Make small changes before complex modifications
4. **Keep Backup**: Save working configuration before testing

## Requirements

- **Android API 28+** (Android 9.0 or higher)
- **Development Tools**: Android Studio with Device File Explorer
- **Device Setup**: USB debugging enabled for file access
- **Dependencies**: voboost-config library (included automatically)

## Project Structure

```
voboost-config-demo/
├── src/main/
│   ├── java/ru/voboost/config/demo/
│   │   └── MainActivity.kt              # Main demonstration activity
│   ├── res/
│   │   ├── layout/activity_main.xml     # UI layout
│   │   ├── values/colors.xml            # Color definitions
│   │   └── drawable/                    # Background styling
│   └── assets/config.yaml               # Default configuration
├── build.gradle.kts                     # Build configuration
└── memory-bank/                         # Project documentation
```

## Related Projects

- **voboost-config**: Main library project (sibling directory)
- **Documentation**: Complete memory bank with technical details
- **Integration**: Production-ready patterns for real applications

This demo application serves as both a validation of the voboost-config library and a comprehensive educational resource for Android developers implementing configuration management.