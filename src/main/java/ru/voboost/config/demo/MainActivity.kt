package ru.voboost.config.demo

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.voboost.config.ConfigManager
import ru.voboost.config.OnConfigChangeListener
import ru.voboost.config.models.Config
import java.io.File

class MainActivity : AppCompatActivity(), OnConfigChangeListener {
    private lateinit var configTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var reloadButton: Button
    private val configManager = ConfigManager(this)
    private val configFileName = "config.yaml"
    private var lastValidConfig: Config? = null
    private var lastValidDiff: Config? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        initializeConfigManager()
    }

    private fun initViews() {
        configTextView = findViewById(R.id.configTextView)
        statusTextView = findViewById(R.id.statusTextView)
        reloadButton = findViewById(R.id.reloadButton)
    }

    private fun setupListeners() {
        reloadButton.setOnClickListener {
            reloadConfig()
        }
    }

    private fun initializeConfigManager() {
        copyDefaultConfigIfNeeded()
        loadAndDisplayConfig()
        startWatchingConfig()
    }

    private fun copyDefaultConfigIfNeeded() {
        val result = configManager.copyDefaultConfigIfNeeded()
        result.fold(
            onSuccess = {
                showToast("Default configuration ready")
            },
            onFailure = { error ->
                showToast("Failed to setup configuration: ${error.message}")
            }
        )
    }

    private fun startWatchingConfig() {
        val result = configManager.startWatching(this)

        result.fold(
            onSuccess = {
                showToast("Started watching configuration file")
            },
            onFailure = { error ->
                showToast("Failed to start watching: ${error.message}")
            }
        )
    }

    private fun loadAndDisplayConfig() {
        val result = configManager.loadConfig()

        result.fold(
            onSuccess = { config ->
                Log.d("MainActivity", "Configuration loaded successfully: $config")
                lastValidConfig = config
                lastValidDiff = null // Reset diff when manually reloading
                displayConfig()
                showStatus("Configuration loaded successfully", StatusType.SUCCESS)
            },
            onFailure = { error ->
                Log.e("MainActivity", "Failed to load configuration", error)
                showConfigError("Error loading configuration:\n${error.message}")
                showStatus("Failed to load configuration", StatusType.ERROR)
            }
        )
    }

    private fun reloadConfig() {
        loadAndDisplayConfig()
    }

    private fun displayConfig(
        diff: Config? = null
    ) {
        val spannableText = SpannableStringBuilder()

        spannableText.appendLine("=== VOBOOST CONFIG DEMO ===")
        spannableText.appendLine()

        spannableText.appendLine("SETTINGS:")
        appendConfigLine(diff, spannableText, "settingsLanguage")
        appendConfigLine(diff, spannableText, "settingsTheme")
        appendConfigLine(diff, spannableText, "settingsInterfaceShiftX")
        appendConfigLine(diff, spannableText, "settingsInterfaceShiftY")
        spannableText.appendLine()

        spannableText.appendLine("VEHICLE:")
        appendConfigLine(diff, spannableText, "vehicleFuelMode")
        appendConfigLine(diff, spannableText, "vehicleDriveMode")
        spannableText.appendLine()

        spannableText.appendLine("Configuration file location:")
        spannableText.appendLine("${File(dataDir, configFileName).absolutePath}")
        spannableText.appendLine()
        spannableText.appendLine("You can modify the config.yaml file and see changes in real-time!")

        configTextView.text = spannableText
    }

    private fun appendConfigLine(
        diff: Config?,
        spannableText: SpannableStringBuilder,
        fieldPath: String
    ) {
        // Get field value using ConfigManager's method
        val value = configManager.getFieldValue(fieldPath)

        val line = "  $fieldPath: ${value ?: "Not set"}"
        val startIndex = spannableText.length
        spannableText.appendLine(line)

        // Check if this field was changed using ConfigManager's method
        val isChanged = configManager.isFieldChanged(diff, fieldPath)

        // Apply color based on whether this field was changed
        val color =
            if (isChanged) {
                ContextCompat.getColor(this, R.color.config_changed_text) // Red for changed
            } else {
                ContextCompat.getColor(this, android.R.color.black) // Black for unchanged
            }

        spannableText.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            // -1 to exclude the newline
            spannableText.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    // Note: Using ConfigManager.isFieldChanged() method instead of custom implementation
    // This ensures consistency with the library's diff calculation logic

    override fun onConfigChanged(
        newConfig: Config,
        diff: Config
    ) {
        runOnUiThread {
            try {
                lastValidConfig = newConfig
                lastValidDiff = diff
                displayConfig(diff)
                showStatus("Configuration updated", StatusType.UPDATE)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error processing config change", e)
                showConfigError("Error processing configuration: ${e.message}")
                showStatus("Configuration error: ${e.message}", StatusType.ERROR)

                // Keep displaying the last valid config with highlighting if available
                lastValidConfig?.let {
                    displayConfig(lastValidDiff)
                }
            }
        }
    }

    override fun onConfigError(error: Exception) {
        runOnUiThread {
            Log.e("MainActivity", "Configuration parsing error", error)
            showConfigError("Configuration parsing error: ${error.message}")
            showStatus("Configuration file contains errors - keeping previous valid state", StatusType.ERROR)

            // Keep displaying the last valid config if available
            lastValidConfig?.let {
                displayConfig(lastValidDiff)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        configManager.stopWatching()
    }

    private enum class StatusType {
        SUCCESS,
        ERROR,
        UPDATE
    }

    private fun showStatus(
        message: String,
        type: StatusType
    ) {
        val color =
            when (type) {
                StatusType.SUCCESS -> ContextCompat.getColor(this, android.R.color.holo_green_dark)
                StatusType.ERROR -> ContextCompat.getColor(this, android.R.color.holo_red_dark)
                StatusType.UPDATE -> ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            }
        statusTextView.text = message
        statusTextView.setTextColor(color)
    }

    private fun showConfigError(errorMessage: String) {
        val errorText = SpannableStringBuilder()
        errorText.appendLine("=== Voboost CONFIG DEMO ===")
        errorText.appendLine()
        errorText.appendLine("❌ CONFIGURATION ERROR ❌")
        errorText.appendLine()
        errorText.appendLine(errorMessage)
        errorText.appendLine()
        errorText.appendLine("Please fix the configuration file and try again.")
        errorText.appendLine()
        errorText.appendLine("Configuration file location:")
        errorText.appendLine("${File(dataDir, configFileName).absolutePath}")
        errorText.appendLine()
        errorText.appendLine("Expected format:")
        errorText.appendLine("settings-language: en|ru")
        errorText.appendLine("settings-theme: auto|light|dark")
        errorText.appendLine("settings-interface-shift-x: <number>")
        errorText.appendLine("settings-interface-shift-y: <number>")
        errorText.appendLine("vehicle-fuel-mode: intellectual|electric|fuel|save")
        errorText.appendLine("vehicle-drive-mode: eco|comfort|sport|snow|outing|individual")

        // Apply red color to the error message
        val errorColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        errorText.setSpan(
            ForegroundColorSpan(errorColor),
            0,
            errorText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        configTextView.text = errorText
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
