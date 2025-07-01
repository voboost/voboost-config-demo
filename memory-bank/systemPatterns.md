# System Architecture: voboost-config-demo

## Overview

voboost-config-demo implements a clean Android application architecture that demonstrates the integration and usage of the voboost-config library with real-time visual feedback capabilities.

## Architectural Patterns

### 1. Consumer Pattern
**Demo Application as Library Consumer**:
- Integrates voboost-config as external dependency
- Demonstrates proper library usage patterns
- Shows real-world integration scenarios
- Validates library functionality in production context

### 2. Observer Pattern
**Configuration Change Handling**:
- MainActivity implements OnConfigChangeListener directly
- Real-time UI updates via onConfigChanged callback
- Thread-safe UI updates using runOnUiThread
- Event-driven architecture for file change detection
- Direct integration without separate listener objects

### 3. Builder Pattern
**Dynamic UI Construction**:
- SpannableStringBuilder for dynamic text formatting
- Scalable text building with conditional highlighting
- Flexible UI component construction
- Extensible design for new configuration fields

### 4. Resource Management Pattern
**Android Resource Lifecycle**:
- Proper cleanup of file watchers in onDestroy()
- Safe UI thread operations for background updates
- Efficient memory usage with minimal state retention
- Android lifecycle-aware resource management

## Core Components

### Application Layer
```
MainActivity
├── Configuration Display Logic
├── File Watcher Management
├── UI Update Handling
└── Error Management
```

### Integration Layer
```
ConfigManager Integration
├── Library Initialization
├── Configuration Loading
├── File Watching Setup
└── Change Notification Handling
```

### UI Layer
```
User Interface
├── Configuration Text Display
├── Status Message Handling
├── Red Color Highlighting
└── Error State Presentation
```

### File System Layer
```
Android File Operations
├── Asset Configuration Loading
├── Private Directory File Copying
├── File Change Monitoring
└── Storage Management
```

## Configuration Display Architecture

### Text Building System
```kotlin
// Actual implementation from MainActivity.kt
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

    configTextView.text = spannableText
}
```

### Highlighting Logic
```kotlin
// Actual implementation using reflection and field paths
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

// Reflection-based field access
private fun getValueByPath(obj: Any, fieldPath: String): Any? {
    val parts = fieldPath.split(".")
    var current: Any? = obj

    for (part in parts) {
        if (current == null) return null
        val clazz = current::class.java
        val field = clazz.getDeclaredField(part)
        field.isAccessible = true
        current = field.get(current)
    }

    return current
}
```

**Key Features:**
- **Reflection-based Access** - Uses field paths like "settings.language"
- **Dynamic Color Application** - Red for changed fields, black for unchanged
- **Scalable Design** - Easy addition of new configuration fields via field paths
- **Performance Optimized** - Minimal UI updates, precise span application

## Real-time Update Flow

### Change Detection Pipeline
```
File Modification → FileWatcher → ReloadableConfig → ConfigManager → OnConfigChangeListener → UI Update
```

### UI Update Process
1. **Background Detection** - File watcher detects changes
2. **Configuration Parsing** - Library parses updated YAML
3. **Diff Calculation** - Library calculates precise changes
4. **UI Thread Switch** - runOnUiThread for safe UI updates
5. **Visual Update** - Apply red highlighting to changed fields

### Thread Safety
- **Background Operations** - File watching and parsing
- **UI Thread Updates** - All UI modifications on main thread
- **Safe Callbacks** - Proper thread switching in change listeners
- **Resource Cleanup** - Thread-safe watcher disposal

## File System Integration

### Asset Management
```kotlin
// Actual implementation from MainActivity.kt
private fun copyDefaultConfigIfNeeded() {
    val configFile = File(filesDir, configFileName)
    if (!configFile.exists()) {
        try {
            assets.open(configFileName).use { inputStream ->
                FileOutputStream(configFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            showToast("Default configuration copied to app directory")
        } catch (e: IOException) {
            showToast("Failed to copy default configuration: ${e.message}")
        }
    }
}
```

### File Operations
- **Asset Loading** - Read default configuration from app assets
- **Private Storage** - Copy to app's private files directory
- **File Watching** - Monitor private directory for changes
- **Error Handling** - Graceful handling of file operation failures

## Error Handling Architecture

### Result Pattern Integration
```kotlin
// Actual implementation using Result.fold
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

### Error Categories
- **File Not Found** - Missing configuration files
- **Parse Errors** - Invalid YAML syntax or structure
- **Permission Errors** - File system access problems
- **Library Errors** - Configuration loading failures

### Error Presentation
- **User-friendly Messages** - Clear error descriptions
- **Status Display** - Visual indication of error states
- **Recovery Options** - Graceful degradation when possible
- **Debug Information** - Detailed error logging for development

## UI Architecture

### Layout Structure
```xml
<!-- Actual layout from activity_main.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- Header -->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textAlignment="center"
        android:textSize="24sp"
        android:textStyle="bold" />

    <!-- Reload Button -->
    <Button
        android:id="@+id/reloadButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/reload_config" />

    <!-- Scrollable Configuration Display -->
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <TextView
            android:id="@+id/configTextView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:fontFamily="monospace"
            android:background="@drawable/config_text_background" />

    </ScrollView>

    <!-- Instructions -->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/instructions"
        android:textAlignment="center" />

</LinearLayout>
```

### Visual Design Patterns
- **Clean Layout** - Simple, professional appearance
- **Clear Typography** - Readable configuration presentation
- **Status Indicators** - Loading, success, and error states
- **Responsive Design** - Adapts to different screen sizes

### Color Scheme
```xml
<!-- Actual colors from colors.xml -->
<color name="config_changed_text">#D32F2F</color>  <!-- Red for changed fields -->
<color name="config_background">#F5F5F5</color>    <!-- Light gray background -->
<color name="config_border">#E0E0E0</color>        <!-- Border color -->
```

**Color Usage:**
- **Black Text** - Default configuration values (android.R.color.black)
- **Red Highlighting** - Changed configuration fields (#D32F2F)
- **Toast Messages** - System default colors for success/error feedback
- **Material Design 3** - Professional color palette throughout UI

## Performance Considerations

### Memory Management
- **Minimal State** - Activity holds only essential references
- **Efficient Updates** - Only rebuild UI when changes occur
- **Resource Cleanup** - Proper disposal of file watchers
- **Garbage Collection** - Avoid memory leaks in long-running operations

### UI Performance
- **Lazy Updates** - Only update UI when configuration changes
- **Efficient Text Building** - Reuse SpannableStringBuilder when possible
- **Minimal Redraws** - Targeted updates for changed fields only
- **Background Operations** - Keep file operations off UI thread

## Integration Patterns

### Library Dependency
```kotlin
// Gradle dependency configuration
dependencies {
    implementation(project(":voboost-config"))
    // or for external usage:
    // implementation("ru.voboost:voboost-config:1.0.0")
}
```

### Initialization Pattern
```kotlin
// Actual implementation from MainActivity.kt
class MainActivity : AppCompatActivity(), OnConfigChangeListener {

    private lateinit var configTextView: TextView
    private lateinit var reloadButton: Button
    private val configManager = ConfigManager()
    private val configFileName = "config.yaml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        copyDefaultConfigIfNeeded()
        loadAndDisplayConfig()
        startWatchingConfig()
    }

    // MainActivity implements OnConfigChangeListener directly
    override fun onConfigChanged(newConfig: Config, diff: Config) {
        runOnUiThread {
            displayConfig(newConfig, diff)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        configManager.stopWatching()
    }
}
```

## Testing Considerations

### Testable Design
- **Dependency Injection** - ConfigManager can be mocked
- **Separated Concerns** - UI logic separate from configuration logic
- **Observable Behavior** - Clear input/output for testing
- **Error Simulation** - Easy to test error scenarios

### Integration Testing
- **Real File Operations** - Test actual file watching
- **UI Validation** - Verify correct display of configuration
- **Change Detection** - Test real-time update functionality
- **Error Handling** - Validate error state presentation

## Extension Points

### New Configuration Fields
- **Scalable Display** - Easy addition of new configuration sections
- **Automatic Highlighting** - New fields automatically get change detection
- **Type Support** - Support for additional data types
- **UI Adaptation** - Layout adapts to new configuration structure

### Custom UI Components
- **Modular Design** - Easy replacement of display components
- **Theme Support** - Configurable color schemes
- **Layout Options** - Different presentation formats
- **Accessibility** - Support for accessibility features

## Design Principles

1. **Simplicity** - Clean, easy-to-understand implementation
2. **Reliability** - Robust error handling and resource management
3. **Performance** - Efficient UI updates and memory usage
4. **Maintainability** - Clear code structure and separation of concerns
5. **Extensibility** - Easy addition of new features and configuration fields
6. **User Experience** - Immediate visual feedback and professional appearance

This architecture provides a solid foundation for demonstrating configuration management capabilities while maintaining simplicity, reliability, and professional quality.