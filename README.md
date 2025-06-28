# VoBoost Config Demo

This is a comprehensive demo application that showcases the functionality of the `voboost-config` library. The demo provides hands-on examples of configuration loading, real-time updates, and diff tracking with multiple testing methods.

## Features

- **Configuration Loading**: Demonstrates how to load YAML configuration files using the library
- **Real-time Updates**: Shows configuration changes in real-time when the config file is modified
- **Diff Tracking**: Displays which specific fields have changed when configuration is updated
- **User-friendly Interface**: Simple UI to view current configuration and reload manually
- **Multiple Testing Methods**: Support for Logcat monitoring and Device File Explorer testing
- **Error Handling**: Demonstrates proper error handling and recovery

## Quick Start

1. **Install and Run**: Build and install the demo app on your Android device (API 28+)
2. **View Configuration**: The app displays the current configuration loaded from `config.yaml`
3. **Test Real-time Updates**: Use any of the testing methods below to modify configuration
4. **Observe Changes**: Watch the UI update automatically and check Logcat for detailed logs

## Testing Methods

### Method 1: Testing via Logcat (Recommended for Development)

This method provides detailed logging information and is ideal for understanding the library's behavior.

#### Setup
1. Connect your Android device via USB with USB Debugging enabled
2. Open Android Studio or use ADB command line
3. Filter Logcat by the demo app package: `ru.voboost.config.demo`

#### Testing Steps
1. **Launch the app** and observe initial configuration loading logs:
   ```
   D/ConfigDemo: Configuration loaded successfully
   D/ConfigDemo: Language: EN, Theme: AUTO
   D/ConfigDemo: Vehicle - Fuel: INTELLECTUAL, Drive: COMFORT
   ```

2. **Start file watching** (automatic on app start):
   ```
   D/ConfigDemo: Started watching configuration file
   ```

3. **Modify configuration** using Device File Explorer (see Method 2)

4. **Observe change detection logs**:
   ```
   D/ConfigDemo: Configuration changed!
   D/ConfigDemo: Language changed from EN to RU
   D/ConfigDemo: Diff - Language: RU, Theme: null, Fuel: null, Drive: null
   ```

#### What to Look For
- **Loading Success/Failure**: Initial configuration load status
- **File Watching Status**: Whether file watching started successfully
- **Change Detection**: Real-time change notifications
- **Diff Information**: Precise field-level change tracking
- **Error Handling**: How the library handles invalid YAML or missing files

### Method 2: Testing via Device File Explorer

This method allows direct file manipulation and is excellent for testing various configuration scenarios.

#### Accessing the Configuration File

1. **Open Android Studio** and ensure your device is connected
2. **Navigate to Device File Explorer**:
   - View → Tool Windows → Device File Explorer
   - Or use the Device File Explorer tab at the bottom

3. **Locate the configuration file**:
   ```
   /data/data/ru.voboost.config.demo/files/config.yaml
   ```

4. **Download the file**:
   - Right-click on `config.yaml`
   - Select "Save As..." to download to your computer

#### Testing Scenarios

##### Scenario 1: Language Change
1. **Edit the downloaded file**:
   ```yaml
   settings:
     language: ru  # Change from 'en' to 'ru'
     theme: auto
     interface-shift-x: 0
     interface-shift-y: 0

   vehicle:
     fuel-mode: intellectual
     drive-mode: comfort
   ```

2. **Upload the modified file**:
   - Right-click on the `config.yaml` in Device File Explorer
   - Select "Upload..." and choose your modified file

3. **Observe the app**: Language setting should update immediately

##### Scenario 2: Theme Change
1. **Modify theme setting**:
   ```yaml
   settings:
     language: en
     theme: dark  # Change from 'auto' to 'dark'
     interface-shift-x: 0
     interface-shift-y: 0

   vehicle:
     fuel-mode: intellectual
     drive-mode: comfort
   ```

2. **Upload and observe**: Theme preference should update

##### Scenario 3: Vehicle Settings Change
1. **Modify vehicle settings**:
   ```yaml
   settings:
     language: en
     theme: auto
     interface-shift-x: 0
     interface-shift-y: 0

   vehicle:
     fuel-mode: electric-forced  # Change fuel mode
     drive-mode: sport          # Change drive mode
   ```

2. **Upload and observe**: Both vehicle settings should update

##### Scenario 4: Interface Positioning
1. **Modify interface positioning**:
   ```yaml
   settings:
     language: en
     theme: auto
     interface-shift-x: 25   # Add horizontal offset
     interface-shift-y: -10  # Add vertical offset

   vehicle:
     fuel-mode: intellectual
     drive-mode: comfort
   ```

2. **Upload and observe**: Interface positioning values should update

##### Scenario 5: Multiple Changes
1. **Change multiple fields simultaneously**:
   ```yaml
   settings:
     language: ru
     theme: dark
     interface-shift-x: 15
     interface-shift-y: 5

   vehicle:
     fuel-mode: save
     drive-mode: eco
   ```

2. **Upload and observe**: All changed fields should be detected in the diff

#### Error Testing Scenarios

##### Invalid YAML Syntax
1. **Create invalid YAML**:
   ```yaml
   settings:
     language: en
     theme: auto
     interface-shift-x: 0
   # Missing closing for settings section
   vehicle:
     fuel-mode: intellectual
   ```

2. **Upload and observe**: App should handle the error gracefully

##### Invalid Enum Values
1. **Use invalid enum values**:
   ```yaml
   settings:
     language: spanish  # Invalid language
     theme: auto
     interface-shift-x: 0
     interface-shift-y: 0

   vehicle:
     fuel-mode: invalid-mode  # Invalid fuel mode
     drive-mode: comfort
   ```

2. **Upload and observe**: App should handle invalid values

### Method 3: Manual Configuration Testing

Use the app's built-in "Reload Configuration" button to test manual configuration loading.

#### Steps
1. **Modify configuration file** using Device File Explorer
2. **Don't wait for automatic detection**
3. **Tap "Reload Configuration"** button in the app
4. **Observe immediate update** and check Logcat for manual reload logs

## Configuration Structure Reference

The demo uses a comprehensive configuration structure:

### Settings Section
```yaml
settings:
  language: en          # Language: ru, en
  theme: auto          # Theme: auto, light, dark
  interface-shift-x: 0 # Horizontal offset: integer pixels
  interface-shift-y: 0 # Vertical offset: integer pixels
```

### Vehicle Section
```yaml
vehicle:
  fuel-mode: intellectual    # Fuel modes: intellectual, electric, electric-forced, fuel, save
  drive-mode: comfort       # Drive modes: eco, comfort, sport, snow, outing, individual
```

## File Locations

### Configuration File
```
/data/data/ru.voboost.config.demo/files/config.yaml
```

### App Data Directory
```
/data/data/ru.voboost.config.demo/
```

## Library Integration Examples

This demo shows comprehensive integration patterns:

### Basic Configuration Loading
```kotlin
private val configManager = ConfigManager()

private fun loadConfiguration() {
    val result = configManager.loadConfig(this, "config.yaml")
    result.onSuccess { config ->
        Log.d("ConfigDemo", "Configuration loaded successfully")
        displayConfiguration(config)
    }.onFailure { error ->
        Log.e("ConfigDemo", "Failed to load configuration", error)
        showError("Failed to load configuration: ${error.message}")
    }
}
```

### Real-time Configuration Watching
```kotlin
private val configChangeListener = object : OnConfigChangeListener {
    override fun onConfigChanged(newConfig: Config, diff: Config) {
        runOnUiThread {
            Log.d("ConfigDemo", "Configuration changed!")

            // Log specific changes
            diff.settingsLanguage?.let { newLanguage ->
                Log.d("ConfigDemo", "Language changed to: $newLanguage")
            }
            diff.settingsTheme?.let { newTheme ->
                Log.d("ConfigDemo", "Theme changed to: $newTheme")
            }

            // Update UI with new configuration
            displayConfiguration(newConfig)
        }
    }
}

private fun startWatching() {
    val result = configManager.startWatching(this, "config.yaml", configChangeListener)
    result.onSuccess {
        Log.d("ConfigDemo", "Started watching configuration file")
    }.onFailure { error ->
        Log.e("ConfigDemo", "Failed to start watching", error)
    }
}
```

### Lifecycle Management
```kotlin
override fun onDestroy() {
    super.onDestroy()
    configManager.stopWatching()
    Log.d("ConfigDemo", "Stopped watching configuration file")
}
```

## Troubleshooting

### Common Issues

#### File Not Found
- **Symptom**: "Configuration file does not exist" error
- **Solution**: Ensure the app has run at least once to copy the initial config file

#### Permission Denied
- **Symptom**: Cannot access `/data/data/` directory
- **Solution**: Use Device File Explorer in Android Studio, not a file manager app

#### Changes Not Detected
- **Symptom**: File changes don't trigger updates
- **Solution**:
  1. Check Logcat for error messages
  2. Verify file was uploaded correctly
  3. Try manual reload button
  4. Restart the app if necessary

#### Invalid Configuration
- **Symptom**: App shows error after file modification
- **Solution**:
  1. Check YAML syntax
  2. Verify enum values are correct
  3. Restore original configuration if needed

### Debug Tips

1. **Always check Logcat** for detailed error information
2. **Use the manual reload button** to test configuration loading
3. **Start with small changes** before testing complex modifications
4. **Keep a backup** of the working configuration file
5. **Test error scenarios** to understand library behavior

## Requirements

- **Android Version**: Android 9 (API 28) or higher
- **Development Tools**: Android Studio with Device File Explorer
- **Device Setup**: USB Debugging enabled for file access
- **Dependencies**: voboost-config library (automatically included)

## Next Steps

After exploring this demo:

1. **Integrate the library** into your own project
2. **Customize the configuration structure** for your needs
3. **Implement proper error handling** based on the demo patterns
4. **Add configuration validation** for your specific use cases
5. **Consider configuration migration** strategies for app updates